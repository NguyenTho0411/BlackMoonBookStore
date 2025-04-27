package com.shopme.admin.book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.publisher.PublisherService;
import com.shopme.common.entity.Book;
import com.shopme.common.entity.BookImage;
import com.shopme.common.entity.Publisher;

@Controller
public class BookController {

	
		private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);
	@Autowired
	private BookService bookService;
	@Autowired
	private PublisherService publisherService;
	
	@GetMapping("/books")
	public String listAll(Model model) {
		List<Book> listBooks = bookService.listAll();
		model.addAttribute("listBooks", listBooks);
		return "book/books";
	}
	@GetMapping("/books/new")
	public String newBook(Model model) {
		List<Publisher> listPublishers = publisherService.listAll();
		Book book = new Book();
		book.setEnabled(true);
		book.setInStock(true);
		model.addAttribute("listPublishers", listPublishers);
		model.addAttribute("book", book);
		model.addAttribute("pageTitle", "Create new book");
		return "book/book_form";
	}
	
	
	@PostMapping("/books/save")
	public String saveBook(Book book, RedirectAttributes r,@RequestParam("fileImage") MultipartFile mainImageMultipart,
			@RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required=false) String[] detailIDs,
			@RequestParam(name = "detailNames", required=false) String[] detailNames,
			@RequestParam(name = "detailValues", required=false) String[] detailValues,
			@RequestParam(name = "imageIDs", required=false) String[] imageIDs,
			@RequestParam(name = "imageNames", required=false) String[] imageNames) throws IOException {
		
		setMainImageName(mainImageMultipart, book);
		setExistingExtraImageNames(imageIDs, imageNames, book);
		setNewExtraImageNames(extraImageMultiparts,book);
		setProductDetails(detailIDs,detailNames, detailValues, book);
			
			Book savedBook = bookService.save(book);
			saveUploadedImages(mainImageMultipart,extraImageMultiparts,savedBook );
			deleteExtraImagesWereRemovedOnForm(book);

		r.addFlashAttribute("message", "The book has been save successfully");
		return "redirect:/books";
	}
	private void deleteExtraImagesWereRemovedOnForm(Book book) {
		String extraImageDir = "../book-images/"+ book.getId()+"/extras";
		Path dirPath = Paths.get(extraImageDir);
		
		try {
			Files.list(dirPath).forEach(file ->{
				String fileName = file.toFile().getName();
				if(!book.containsImageName(fileName)) {
					try {
						Files.delete(file);
						LOGGER.info("Delete extra image: "+fileName);
					}catch(IOException e) {
						LOGGER.error("Could not delete extra image: "+fileName);
					}
				}
			});
		}catch(IOException ex) {
			LOGGER.error("Could not list directory: "+dirPath);
		}
		
	}
	private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Book book) {
		// TODO Auto-generated method stub
		if(imageIDs == null || imageIDs.length==0) return;
		
		Set<BookImage> images = new HashSet<>();
		for(int count =0;count< imageIDs.length;count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			images.add(new BookImage(id,name,book));
		}
		book.setImages(images);
		
	}
	private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Book book) {
		if(detailNames == null || detailNames.length ==0 ) return;
		
		
		for(int count =0; count< detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			if(id != 0) {
				book.addDetail(id, name,value);
			}else if(!name.isEmpty() && !value.isEmpty()) {
				book.addDetail(name,value);
			}
		}
		
	}
	private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
			Book savedBook) throws IOException {
		if(!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "../book-images/"+ savedBook.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		if(extraImageMultiparts.length >0) {
			String uploadDir = "../book-images/"+ savedBook.getId()+"/extras";
			for(MultipartFile multipartFile : extraImageMultiparts) {
				if(multipartFile.isEmpty())  continue;
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}

	}
	}
	private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Book book) {
	    if (extraImageMultiparts.length > 0) {
	        for (MultipartFile multipartFile : extraImageMultiparts) {
	            if (!multipartFile.isEmpty()) {
	                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
	                book.addExtraImage(fileName); // ❌ KHÔNG cần kiểm tra contains
	            }
	        }
	    }
	}

	private void setMainImageName(MultipartFile mainImageMultipart, Book book) {
		if(!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			book.setMainImage(fileName);
		}
	}
	@GetMapping("/books/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, 
			Model model, RedirectAttributes redirectAttributes) {
		try {
			bookService.delete(id);
			String productExtraImagesDir = "../book-images/" + id + "/extras";
			String productImagesDir = "../book-images/" + id;
			
			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productImagesDir);
			
			redirectAttributes.addFlashAttribute("message", 
					"The book ID " + id + " has been deleted successfully");
		} catch (BookNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/books";
	}
	
	@GetMapping("/books/edit/{id}")
	public String editBook(@PathVariable("id") Integer id, Model model, RedirectAttributes r) {
		try {
			Book book = bookService.get(id);

			Integer numberOfExistingExtraImages = book.getImages().size();
			List<Publisher> listPublishers = publisherService.listAll();
			model.addAttribute("book", book);
			model.addAttribute("listPublishers",listPublishers);
			model.addAttribute("pageTitle", "Edit Product (ID: "+id+")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			return "book/book_form";
		} catch (BookNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			r.addFlashAttribute("message", e.getMessage());
			return "redirect:/books";
		}
	}
	
	@GetMapping("/books/detail/{id}")
	public String viewBookDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes r) {
		try {
			Book book = bookService.get(id);
			model.addAttribute("book", book);

			return "book/book_detail_modal";
		} catch (BookNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			r.addFlashAttribute("message", e.getMessage());
			return "redirect:/books";
		}
	}
}
