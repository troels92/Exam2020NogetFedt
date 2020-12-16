package com.nogetfedt.kea.controller;

import com.nogetfedt.kea.model.IntComparer;
import com.nogetfedt.kea.model.Picture;
import com.nogetfedt.kea.model.Product;
import com.nogetfedt.kea.repository.NameSorter;
import com.nogetfedt.kea.repository.PriceSorter;
import com.nogetfedt.kea.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    ProductRepo repo;

    //Show frontpage
    @RequestMapping("/")
    public String index(Model model)
    {
        model.addAttribute("products", repo.findAll());
        return "index";
    }

    //Create
            @RequestMapping("/addProduct")
            public String addProduct()
            {
                return "addProduct";
            }

            //Executing save
            @RequestMapping("/newProduct")
            public String newProduct(Product product)
            {
                repo.save(product);
                return "picUpload";
            }

    @RequestMapping("/picUpload")
    public String picUpload()
    {
        return "picUpload";
    }

    @PostMapping("/save")
    public String save(@RequestParam("photo")MultipartFile photo, ModelMap model){

        Picture picture = new Picture();


        Path path = Paths.get("./src/main/resources/static/img/");

        try {
            InputStream inputStream = photo.getInputStream();
            Files.copy(inputStream, path.resolve(photo.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            picture.setPhoto(photo.getOriginalFilename().toLowerCase());
            //

            model.addAttribute("PICTURE", picture);

        } catch (Exception e){
            e.printStackTrace();
        }

        return "addProduct";
    }



    //Read
        @ModelAttribute
        @GetMapping("/view")
        public String viewProduct(Model model)
        {
            model.addAttribute("products", repo.findAll());

            return "/view";
        }

        @PostMapping("/viewSearch")
        public String viewSearchProduct(WebRequest request, Model model)
        {
            List<Product> viewList = repo.findAll();


            int id = Integer.parseInt(request.getParameter("searchId"));

            if(id == 1)
            {
                viewList.sort(new PriceSorter());
                model.addAttribute("products", viewList);

                return "/view";
            }
            if(id == 2)
            {
                viewList.sort(new NameSorter());
                model.addAttribute("products", viewList);

                return "/view";
            }
            return "redirect:/view";
        }

        //Update

        @GetMapping("/edit/{id}")
        public String showUpdateForm(@PathVariable("id") int id, Model model) {
            Product product = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

            model.addAttribute("product", product);
            return "update-product";
        }

        @PostMapping("/update/{id}")
        public String updateProduct(@PathVariable("id") int id, @Valid Product product,
                                    BindingResult result, Model model) {
            if (result.hasErrors()) {
                product.setProduct_Id(id);
                return "update-product";
            }

            repo.save(product);
            model.addAttribute("product", repo.findAll());
            return "redirect:/index";
        }


        //Delete
        @RequestMapping("/deleteProduct")
        public String deleteProduct(Model model){return "deleteProduct";}

        @PostMapping("/deleteProduct")
        public String deleteProductPost(@ModelAttribute IntComparer intComparer){
            if (intComparer.compare()){
                repo.deleteById(intComparer.getInt1());}
            else{return "deleteProductFailed";}
            return "deleteProduct";}
        //modtag 2 ints fra model
        //hvis de matcher, så prøv at slette produktet med den ID

}
