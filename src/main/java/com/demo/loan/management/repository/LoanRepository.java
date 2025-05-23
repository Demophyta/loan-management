package com.demo.loan.management.repository;

import com.demo.loan.management.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserUserId(Long userId);
    @Query("SELECT COUNT(l) > 0 FROM Loan l WHERE l.user.userId = :userId AND l.loanType = :loanType AND l.loanStatus IN ('PENDING', 'APPROVED')")
    boolean existsActiveLoan(@Param("userId") Long userId, @Param("loanType") String loanType);
    Optional<Loan> findByUserUserIdAndLoanTypeAndLoanStatus(Long userId, String loanType, String loanStatus);
    List<Loan> findByLoanStatus(String loanStatus);
}
