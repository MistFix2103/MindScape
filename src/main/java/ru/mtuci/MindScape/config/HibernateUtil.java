/**
 * <p>Описание:</p>
 * Утилитный класс для управления Hibernate SessionFactory. Используется для создания и закрытия SessionFactory.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>sessionFactory: Статическая фабрика сессий Hibernate.</li>
 * </ul>
 *
 * <p>Список методов:</p>
 * <ul>
 *     <li>
 *         <b>buildSessionFactory</b> - Приватный статический метод для создания новой SessionFactory.
 *         Инициализируется при загрузке класса.
 *     </li>
 *     <li>
 *         <b>shutdown</b> - Статический метод для закрытия SessionFactory.
 *     </li>
 * </ul>
 */

package ru.mtuci.MindScape.config;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}