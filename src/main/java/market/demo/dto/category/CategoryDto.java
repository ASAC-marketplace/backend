package market.demo.dto.category;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDto {
    private Long id;
    private final String name;
    private List<SubCategoryDto> subCategories;

    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
