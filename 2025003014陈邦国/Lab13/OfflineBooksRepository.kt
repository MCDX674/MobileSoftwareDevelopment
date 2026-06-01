package com.example.myapplicationlab10.bookshelf.data

import com.example.myapplicationlab10.bookshelf.model.Book

/**
 * 离线仓库实现类
 * 实现BooksRepository接口
 * 内置静态模拟数据
 * 断网或网络异常时自动切换
 * 保证应用正常运行
 */
class OfflineBooksRepository : BooksRepository {
    private val mockBooks = listOf(
        Book("1", "https://picsum.photos/id/10/800/600", "Book 1"),
        Book("2", "https://picsum.photos/id/11/800/600", "Book 2"),
        Book("3", "https://picsum.photos/id/12/800/600", "Book 3"),
        Book("4", "https://picsum.photos/id/13/800/600", "Book 4")
    )

    override suspend fun getBooks(): List<Book> = mockBooks

    override suspend fun getBook(id: String): Book {
        return mockBooks.first { it.id == id }
    }
}