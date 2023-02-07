package com.example.smsweb.api.service;

import com.example.smsweb.api.di.irepository.INews;
import com.example.smsweb.api.di.repository.NewsRepository;
import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService implements INews {
    @Autowired
    private NewsRepository dao;

    @Override
    public void save(News news) {
        try {
            dao.save(news);
        } catch (Exception e) {
            throw new ErrorHandler("Sao lưu thất bại");
        }
    }

    @Override
    public void delete(int id) {
        try {
            dao.deleteById(id);
        } catch (Exception e) {
            throw new ErrorHandler("Xóa thất bại");
        }
    }

    @Override
    public List<News> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new ErrorHandler("Không tìm thấy dữ liệu");
        }
    }

    @Override
    public News findOne(int id) {
        try {
            return dao.findById(id).get();
        }catch (Exception e){
            throw new ErrorHandler("Không tìm thấy dữ liệu với id " + id);
        }
    }
}
