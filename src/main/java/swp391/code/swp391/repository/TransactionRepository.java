package swp391.code.swp391.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp391.code.swp391.entity.Transaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser_UserId(Long userId);
    List<Transaction> findBySession_SessionId(Long sessionId);
    List<Transaction> findByStatus(Transaction.Status status);
    Optional<Transaction> findBySession_SessionIdAndStatus(Long sessionId, Transaction.Status status);
}
