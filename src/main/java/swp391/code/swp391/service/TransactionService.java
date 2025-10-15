package swp391.code.swp391.service;

import swp391.code.swp391.dto.TransactionDTO;
import swp391.code.swp391.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Long sessionId, Long userId, Double amount, Transaction.PaymentMethod paymentMethod);
    Transaction getTransactionById(Long transactionId);
    Transaction updateTransactionStatus(Long transactionId, Transaction.Status status);
    List<TransactionDTO> getTransactionsByUserId(Long userId);
    List<TransactionDTO> getTransactionsBySessionId(Long sessionId);
    TransactionDTO convertToDTO(Transaction transaction);
}
