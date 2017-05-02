package com.github.barteksc.sample.adapter;


import android.graphics.pdf.PdfRenderer;
import android.view.View;

/**
 * Created by admin on 2016/5/9.
 */
public class Book {
    PdfRenderer mPdfRenderer=null;
    private Long id;
    private View saveView;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String path;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    private int pages;
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public PdfRenderer getmPdfRenderer() {
        return mPdfRenderer;
    }

    public void setmPdfRenderer(PdfRenderer mPdfRenderer) {
        this.mPdfRenderer = mPdfRenderer;
    }
    /**
     *
     */
    public Book() {
        super();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public View getSaveView() {
        return saveView;
    }

    public void setSaveView(View saveView) {
        this.saveView = saveView;
    }


}