package com.baron.learning

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ProductServiceController {

    companion object {
        private val productRepo = HashMap<String, Product>()
        init {
            val honey = Product("1", "Honey")
            productRepo[honey.id] = honey
            val almond = Product("2", "Almond")
            productRepo[almond.id] = almond
        }
    }

    @RequestMapping(value = ["/products"])
    fun getProduct() : ResponseEntity<Any> {
        return ResponseEntity(productRepo.values, HttpStatus.OK)
    }

    @RequestMapping(value = ["products"], method = [RequestMethod.POST])
    fun createProduct(@RequestBody product: Product) : ResponseEntity<Any> {
        productRepo[product.id] = product
        return ResponseEntity(BaseResponse("Product is created successfully"), HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/products/{id}"], method = [RequestMethod.PUT])
    fun updateProduct(@PathVariable("id") id: String, @RequestBody product: Product) : ResponseEntity<Any> {
        productRepo.remove(id)
        product.id = id
        productRepo[id] = product
        return ResponseEntity(BaseResponse("Product is updated successfully"), HttpStatus.OK)
    }

    @RequestMapping(value = ["/products/{id}"], method = [RequestMethod.DELETE])
    fun deleteProduct(@PathVariable("id") id: String): ResponseEntity<Any> {
        productRepo.remove(id)
        return ResponseEntity(BaseResponse("Product is deleted successfully"), HttpStatus.OK)
    }
}