//package com.cartechindia.util;
//
//import com.cartechindia.dto.PageResponse;
//import org.springframework.data.domain.Page;
//
//public class PageResponseMapper {
//
//    // 🔒 Private constructor to prevent instantiation
//    private PageResponseMapper() {
//        throw new UnsupportedOperationException("Utility class cannot be instantiated");
//    }
//
//    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
//        PageResponse<T> response = new PageResponse<>();
//        response.setContent(page.getContent());
//        response.setNumber(page.getNumber());
//        response.setSize(page.getSize());
//        response.setTotalElements(page.getTotalElements());
//        response.setTotalPages(page.getTotalPages());
//        response.setFirst(page.isFirst());
//        response.setLast(page.isLast());
//        return response;
//    }
//}
