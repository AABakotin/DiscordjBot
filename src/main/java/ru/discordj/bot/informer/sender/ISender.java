package ru.discordj.bot.informer.sender;

import java.util.Map;

/**
 * Интерфейс для отправки и получения данных от игровых серверов.
 * Реализует базовый функционал для взаимодействия с серверами по протоколу UDP.
 */
public interface ISender {

    /**
     * Получает информацию от игрового сервера.
     * 
     * @return Map с информацией о сервере, где ключ - название параметра, значение - его значение.
     *         В случае ошибки или если сервер не отвечает, возвращает null.
     */
    Map<String, String> receive();

    /**
     * Отправляет данные на игровой сервер.
     * 
     * @param data массив байт для отправки на сервер
     * @throws RuntimeException если произошла ошибка при отправке данных
     */
    void send(byte[] data);
}