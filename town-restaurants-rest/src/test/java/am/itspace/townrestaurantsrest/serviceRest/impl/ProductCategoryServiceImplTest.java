package am.itspace.townrestaurantsrest.serviceRest.impl;

import am.itspace.townrestaurantscommon.dto.productCategory.ProductCategoryOverview;
import am.itspace.townrestaurantscommon.entity.ProductCategory;
import am.itspace.townrestaurantscommon.mapper.ProductCategoryMapper;
import am.itspace.townrestaurantscommon.repository.ProductCategoryRepository;
import am.itspace.townrestaurantsrest.exception.EntityAlreadyExistsException;
import am.itspace.townrestaurantsrest.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static am.itspace.townrestaurantsrest.parameters.MockData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceImplTest {

    @InjectMocks
    ProductCategoryServiceImpl productCategoryService;

    @Mock
    ProductCategoryMapper categoryMapper;

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @Test
    void shouldSaveProductCategory() {
        //given
        var productCategory = getProductCategory();
        var expected = getProductCategoryOverview();
        var createProductCategory = getCreateProductCategoryDto();
        //when
        doReturn(false).when(productCategoryRepository).existsByName(anyString());
        doReturn(productCategory).when(categoryMapper).mapToEntity(createProductCategory);
        doReturn(expected).when(categoryMapper).mapToOverview(productCategory);
        doReturn(productCategory).when(productCategoryRepository).save(any(ProductCategory.class));
        ProductCategoryOverview actual = productCategoryService.save(createProductCategory);
        //then
        verify(productCategoryRepository, times(1)).save(productCategory);
        assertNotNull(actual);
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldThrowExceptionAsNameAlreadyExists() {
        //given
        var createProductCategory = getCreateProductCategoryDto();
        //when
        doReturn(true).when(productCategoryRepository).existsByName(anyString());
        //then
        assertThrows(EntityAlreadyExistsException.class, () -> productCategoryService.save(createProductCategory));
    }

    @Test
    void saveShouldThrowEntityAlreadyExistsException() {
        //given
        var createProductCategory = getCreateProductCategoryDto();
        //when
        doThrow(EntityAlreadyExistsException.class).when(productCategoryRepository).existsByName(anyString());
        //then
        assertThrows(EntityAlreadyExistsException.class, () -> productCategoryService.save(createProductCategory));
    }

    @Test
    void shouldGetAllCategories() {
        //given
        var listOfCategoriesPage = getPageProductCategories();
        var expected = List.of(getProductCategoryOverview(), getProductCategoryOverview());
        PageRequest pageable = PageRequest.of(1, 1, Sort.Direction.fromString("DESC"), "name");
        //when
        doReturn(listOfCategoriesPage).when(productCategoryRepository).findAll(pageable);
        doReturn(expected).when(categoryMapper).mapToOverviewList(anyList());
        List<ProductCategoryOverview> actual = productCategoryService.getAllCategories(1, 1, "name", "DESC");
        //then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getAllCategoriesShouldThrowException() {
        //given
        var emptyCategories = getEmptyProductCategories();
        PageRequest pageable = PageRequest.of(1, 1, Sort.Direction.fromString("DESC"), "name");
        //when
        doReturn(emptyCategories).when(productCategoryRepository).findAll(pageable);
        //then
        assertThrows(EntityNotFoundException.class, () -> productCategoryService.getAllCategories(1, 1, "name", "DESC"));
    }

    @Test
    void shouldGetById() {
        //given
        var productCategory = getProductCategory();
        var expected = getProductCategoryOverview();
        //when
        doReturn(Optional.of(productCategory)).when(productCategoryRepository).findById(anyInt());
        doReturn(expected).when(categoryMapper).mapToOverview(productCategory);
        ProductCategoryOverview actual = productCategoryService.getById(anyInt());
        //then
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    void shouldEntityNotFoundExceptionAsProductCategoryNotFoundById() {
        //when
        doThrow(EntityNotFoundException.class).when(productCategoryRepository).findById(anyInt());
        //then
        assertThrows(EntityNotFoundException.class, () -> productCategoryService.getById(anyInt()));
    }

    @Test
    void shouldUpdateProductCategory() {
        //given
        var productCategory = getProductCategory();
        var expected = getProductCategoryOverview();
        var editProductCategoryDto = getEditProductCategoryDto();
        //when
        doReturn(Optional.of(productCategory)).when(productCategoryRepository).findById(anyInt());
        doReturn(expected).when(categoryMapper).mapToOverview(productCategory);
        doReturn(productCategory).when(productCategoryRepository).save(any(ProductCategory.class));
        ProductCategoryOverview actual = productCategoryService.update(anyInt(), editProductCategoryDto);
        //then
        verify(productCategoryRepository, times(1)).save(productCategory);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void deleteSuccess() {
        //given
        int productId = 1;
        //when
        when(productCategoryRepository.existsById(productId)).thenReturn(true);
        productCategoryService.delete(productId);
        //then
        verify(productCategoryRepository).deleteById(productId);
    }

    @Test
    void shouldThrowExceptionAsProductCategoryNotFound() {
        //given
        int productCategoryId = 1;
        //when
        when(productCategoryRepository.existsById(productCategoryId)).thenReturn(false);
        //then
        assertThrows(EntityNotFoundException.class, () -> productCategoryService.delete(productCategoryId));
    }
}