package uz.test.bot.testBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.test.bot.testBot.entity.Answer;
@Repository
public interface AnswerRepository extends JpaRepository<Answer,Long> {
}
