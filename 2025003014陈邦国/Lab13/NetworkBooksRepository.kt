package com.example.myapplicationlab10.bookshelf.data

import com.example.myapplicationlab10.bookshelf.model.Book
import com.example.myapplicationlab10.bookshelf.model.BookDto
import com.example.myapplicationlab10.bookshelf.network.BookshelfApiService

/**
 * 网络仓库实现类
 * 实现BooksRepository接口
 * 依赖Retrofit ApiService
 * 联网时优先使用
 * 调用接口获取DTO并转换为领域模型
 */
class NetworkBooksRepository(
    private val apiService: BookshelfApiService
) : BooksRepository {
    override suspend fun getBooks(): List<Book> {
        val dtoList = apiService.getBooks()
        return dtoList.map { it.asDomainModel() }
    }

    override suspend fun getBook(id: String): Book {
        return getBooks().first { it.id == id }
    }
}