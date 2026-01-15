package com.ubs.expensemanager.service;

import com.ubs.expensemanager.domain.Category;
import com.ubs.expensemanager.dto.category.CreateCategoryRequest;
import com.ubs.expensemanager.dto.category.UpdateCategoryRequest;
import com.ubs.expensemanager.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_valid_shouldSave() {
        CreateCategoryRequest req = mock(CreateCategoryRequest.class);
        when(req.name()).thenReturn("Travel");
        when(req.dailyLimit()).thenReturn(BigDecimal.valueOf(10));
        when(req.monthlyLimit()).thenReturn(BigDecimal.valueOf(300));

        when(repository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            if (c.getId() == null) c.setId(UUID.randomUUID());
            return c;
        });

        Category saved = categoryService.createCategory(req);

        assertNotNull(saved.getId());
        assertEquals("Travel", saved.getName());
        assertEquals(BigDecimal.valueOf(10), saved.getDailyLimit());
        assertEquals(BigDecimal.valueOf(300), saved.getMonthlyLimit());
        verify(repository, times(1)).save(any());
    }

    @Test
    void createCategory_nullLimits_shouldThrow() {
        CreateCategoryRequest req = mock(CreateCategoryRequest.class);
        when(req.name()).thenReturn("X");
        when(req.dailyLimit()).thenReturn(null);
        when(req.monthlyLimit()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(req));
        verify(repository, never()).save(any());
    }

    @Test
    void createCategory_negativeLimits_shouldThrow() {
        CreateCategoryRequest req = mock(CreateCategoryRequest.class);
        when(req.name()).thenReturn("X");
        when(req.dailyLimit()).thenReturn(BigDecimal.valueOf(-1));
        when(req.monthlyLimit()).thenReturn(BigDecimal.valueOf(0));

        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(req));
        verify(repository, never()).save(any());
    }

    @Test
    void createCategory_monthlyLessThanDaily_shouldThrow() {
        CreateCategoryRequest req = mock(CreateCategoryRequest.class);
        when(req.name()).thenReturn("X");
        when(req.dailyLimit()).thenReturn(BigDecimal.valueOf(100));
        when(req.monthlyLimit()).thenReturn(BigDecimal.valueOf(50));

        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(req));
        verify(repository, never()).save(any());
    }

    @Test
    void updateCategory_partialUpdate_shouldSaveUpdatedFields() {
        UUID id = UUID.randomUUID();
        Category existing = new Category();
        existing.setId(id);
        existing.setName("Old");
        existing.setDailyLimit(BigDecimal.valueOf(5));
        existing.setMonthlyLimit(BigDecimal.valueOf(150));

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCategoryRequest req = mock(UpdateCategoryRequest.class);
        when(req.name()).thenReturn("New");
        when(req.dailyLimit()).thenReturn(null);
        when(req.monthlyLimit()).thenReturn(BigDecimal.valueOf(200));

        Category updated = categoryService.updateCategory(id, req);

        assertEquals("New", updated.getName());
        assertEquals(BigDecimal.valueOf(5), updated.getDailyLimit()); // unchanged
        assertEquals(BigDecimal.valueOf(200), updated.getMonthlyLimit());
        verify(repository, times(1)).save(existing);
    }

    @Test
    void findById_notFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> categoryService.findById(id));
    }

    @Test
    void findAll_delegatesToRepository() {
        List<Category> list = Arrays.asList(new Category(), new Category());
        when(repository.findAll()).thenReturn(list);
        List<Category> res = categoryService.findAll();
        assertEquals(2, res.size());
    }
}
