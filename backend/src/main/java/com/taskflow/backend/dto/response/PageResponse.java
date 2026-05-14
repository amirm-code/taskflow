package com.taskflow.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;        // les éléments de la page
    private int currentPage;        // numéro de la page (commence à 0)
    private int pageSize;           // taille de la page
    private long totalElements;     // nombre total d'éléments
    private int totalPages;         // nombre total de pages
    private boolean first;          // première page ?
    private boolean last;           // dernière page ?
}