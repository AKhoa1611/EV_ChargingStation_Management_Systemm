package swp391.code.swp391.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swp391.code.swp391.dto.TransactionDTO;
import swp391.code.swp391.entity.Session;
import swp391.code.swp391.entity.Transaction;
import swp391.code.swp391.entity.User;
import swp391.code.swp391.repository.SessionRepository;
import swp391.code.swp391.repository.TransactionRepository;
import swp391.code.swp391.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    
    @Override
    public Transaction createTransaction(Long sessionId, Long userId, Double amount, Transaction.PaymentMethod paymentMethod) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Transaction transaction = new Transaction();
        transaction.setSession(session);
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setStatus(Transaction.Status.PENDING);
        
        return transactionRepository.save(transaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
    }
    
    @Override
    public Transaction updateTransactionStatus(Long transactionId, Transaction.Status status) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsBySessionId(Long sessionId) {
        return transactionRepository.findBySession_SessionId(sessionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setSessionId(transaction.getSession().getSessionId());
        dto.setUserId(transaction.getUser().getUserId());
        dto.setUserEmail(transaction.getUser().getEmail());
        dto.setUserName(transaction.getUser().getFullName());
        dto.setAmount(transaction.getAmount());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setStatus(transaction.getStatus());
        return dto;
    }
}
