package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseCategoryResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseCategoryResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.mappers.CourseCategoryResponseMapperImpl;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseCategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CourseCategoryServiceTest {

    @Mock
    private CourseCategoryRepository courseCategoryRepository;
    @Spy
    private final CourseCategoryResponseMapper courseCategoryResponseMapper = new CourseCategoryResponseMapperImpl();
    @InjectMocks
    private CourseCategoryService courseCategoryService;

    @Test
    public void getCourseCategoryList_shouldReturnCourseCategoryListResponse_whenCourseCategoriesExists(){

        //Arrange
        CourseCategory category1 = CourseCategory.builder()
                .id(UUID.randomUUID())
                .name("java")
                .slug("java")
                .build();

        CourseCategory category2 = CourseCategory.builder()
                .id(UUID.randomUUID())
                .name("react")
                .slug("react")
                .build();

        List<CourseCategory> courseCategoryList = List.of(category1, category2);

        Mockito.when(courseCategoryRepository.findAll())
                .thenReturn(courseCategoryList);

        //Act
        List<CourseCategoryResponse> result = courseCategoryService.getCourseCategoryList();

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(2);

        Assertions.assertThat(result.get(0).id()).isEqualTo(category1.getId());
        Assertions.assertThat(result.get(0).name()).isEqualTo(category1.getName());
        Assertions.assertThat(result.get(0).slug()).isEqualTo(category1.getSlug());

        Assertions.assertThat(result.get(1).id()).isEqualTo(category2.getId());
        Assertions.assertThat(result.get(1).name()).isEqualTo(category2.getName());
        Assertions.assertThat(result.get(1).slug()).isEqualTo(category2.getSlug());

        Mockito.verify(courseCategoryRepository).findAll();
        Mockito.verify(courseCategoryResponseMapper).toCourseCategoryResponseList(courseCategoryList);
    }

    @Test
    public void getCourseCategoryList_shouldReturnEmptyList_whenCourseCategoriesDoNotExist(){

        //Arrange
        List<CourseCategory> courseCategoryList = List.of();

        Mockito.when(courseCategoryRepository.findAll())
                .thenReturn(courseCategoryList);

        //Act
        List<CourseCategoryResponse> result = courseCategoryService.getCourseCategoryList();

        //Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEmpty();

        Mockito.verify(courseCategoryRepository).findAll();
        Mockito.verify(courseCategoryResponseMapper).toCourseCategoryResponseList(courseCategoryList);
    }

}
