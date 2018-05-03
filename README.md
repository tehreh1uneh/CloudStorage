# CloudStorage

Проект носит академический характер и направлен на демонстрацию знаний по Java, полученных в процессе обучения на [GeekBrains](https://geekbrains.ru) .

###### Запуск проекта осуществляется из двух мест:
1. Серверное приложение: /serverGUI/src/main/java/com/tehreh1uneh/cloudstorage/servergui/screenmanager/ServerApp.java
2. Клиентское приложение: /client/src/main/java/com/tehreh1uneh/cloudstorage/client/screenmanager/ClientApp.java

###### Общее описание:
Упрощённая версия облачного хранилища. Сервер хранит у себя файлы пользователей. Пользователи через клиент могут манипулировать файлами.

###### Функционал:
1. Передача файлов на сервер
2. Скачивание файлов с сервера
3. Переименование файлов на сервере
4. Удаление файлов с сервера
5. Передача файлов с использованием drag and drop
6. Создание папок на сервере
7. Навигация по иерархии папок на сервере
  
Серверное приложение:  
![Server start](https://github.com/tehreh1uneh/CloudStorage/blob/master/Cloud_storage_server_start.png)  
  
Регистрация:  
![Registration](https://github.com/tehreh1uneh/CloudStorage/blob/master/Cloud_storage_client_registration.png)  
  
Авторизация:  
![Authentication](https://github.com/tehreh1uneh/CloudStorage/blob/master/Cloud_storage_client_auth.png)  
  
Рабочая область:  
![Workspace](https://github.com/tehreh1uneh/CloudStorage/blob/master/Cloud_storage_client_drag_and_drop.png)  
  
Переименование:  
![Rename](https://github.com/tehreh1uneh/CloudStorage/blob/master/Cloud_storage_client_rename.png)  
  
###### Планы доработок:
1. Активация соседней строки после удаления файла
2. Контекстное меню со всеми командами
3. Замена кнопок приятными картинками + всплывающие подсказки по функционалу
4. Лого в шапку окон
5. Максимальный уход от IO пакета в NIO пакет
6. Разбивка ErrorMessage на Error и Info
7. Добавление визуальных приятностей  из пакета ControlsFX (красные крестики около незаполненных полей)
8. Групповые операции с файлами (множественное выделение строк)
9. Добавление подтверждения пользователем перед удалением файла
10. Перемещение файлов
11. Корзина 
12. Пул соединений на сервере и обрыв всех при отключении сервера
13. Окна настроек для сервера и клиента
14. Подгонка архитектуры под MVP
15. Локализация en-ru
