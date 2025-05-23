package com.demo.loan.management.repository;

import com.demo.loan.management.model.Emi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmiRepository extends JpaRepository<Emi, Long> {


  //Get all EMIs for a specific loan.

    List<Emi> findByLoanLoanId(Long loanId);

    //Count PENDING EMIs for a loan (Used to check if loan is fully repaid).

    long countByLoanLoanIdAndStatus(Long loanId, String status);

    //Find all unpaid EMIs (for tracking due payments).

    List<Emi> findByStatus(String status);

 //Find EMIs for a specific user and their loan status.

    List<Emi> findByLoanUserUserIdAndStatus(Long userId, String status);

    /**
      Find EMIs by month and year for a specific loan.
      This can be used to filter by specific month/year for EMI payments.
     */
    List<Emi> findByLoanLoanIdAndDueDateBetween(Long loanId, LocalDate startDate, LocalDate endDate);

    /**
     Find all EMIs for a specific user (via their loans).
     */
    List<Emi> findByLoanUserUserId(Long userId);

}
