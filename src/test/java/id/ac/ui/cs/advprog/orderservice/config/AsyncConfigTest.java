package id.ac.ui.cs.advprog.orderservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class AsyncConfigTest {

    @Test
    void testTaskExecutorBean() {
        // Arrange
        AsyncConfig asyncConfig = new AsyncConfig();

        // Act
        Executor executor = asyncConfig.taskExecutor();

        // Assert
        assertNotNull(executor);
        assertInstanceOf(ThreadPoolTaskExecutor.class, executor);
        
        ThreadPoolTaskExecutor threadPoolExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(2, threadPoolExecutor.getCorePoolSize());
        assertEquals(5, threadPoolExecutor.getMaxPoolSize());
        assertEquals(100, threadPoolExecutor.getQueueCapacity());
        assertTrue(threadPoolExecutor.getThreadNamePrefix().startsWith("OrderAsync-"));
    }
} 