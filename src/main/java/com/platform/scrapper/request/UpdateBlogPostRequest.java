package com.platform.scrapper.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBlogPostRequest {
    
    @NotBlank(message = "Blog title is required")
    @Size(min = 5, max = 500, message = "Blog title must be between 5 and 500 characters")
    private String blogTitle;
    
    @NotBlank(message = "Blog content is required")
    @Size(min = 10, message = "Blog content must be at least 10 characters")
    private String content;
    
    private String excerpt;
    
    private String featuredImage;
    
    private String author;
}






