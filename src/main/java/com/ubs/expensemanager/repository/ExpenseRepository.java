package com.ubs.expensemanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ubs.expensemanager.domain.Expense;
import com.ubs.expensemanager.domain.enums.ExpenseStatus;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.employee.department.id = :departmentId
          AND e.date BETWEEN :start AND :end
          AND e.status = :status
    """)
    BigDecimal sumAmountByDepartmentAndMonth(
        @Param("departmentId") UUID departmentId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end,
        @Param("status") ExpenseStatus status
    );
    
    // same sum buut excluding one expense (for the update)
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.employee.department.id = :departmentId
          AND e.date BETWEEN :start AND :end
          AND e.status = :status
          AND (:excludeId IS NULL OR e.id <> :excludeId)
    """)
    BigDecimal sumAmountByDepartmentAndMonthExcluding(
        @Param("departmentId") UUID departmentId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end,
        @Param("status") ExpenseStatus status,
        @Param("excludeId") UUID excludeId
    );
    
    @Query("""
        SELECT e.employee.department.id AS deptId, COALESCE(SUM(e.amount), 0) AS total
        FROM Expense e
        WHERE e.date BETWEEN :start AND :end
          AND e.status = :status
        GROUP BY e.employee.department.id
    """)
    List<Object[]> sumAmountByDepartmentBetween(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end,
        @Param("status") com.ubs.expensemanager.domain.enums.ExpenseStatus status
    );

    @Query("""
    SELECT e
    FROM Expense e
    JOIN FETCH e.employee emp
    LEFT JOIN FETCH emp.department dept
    JOIN FETCH e.category cat
    WHERE e.date BETWEEN :start AND :end
    ORDER BY e.date DESC
""")
    List<Expense> findAllBetweenDates(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    boolean existsByEmployee_Id(UUID employeeId);
}
