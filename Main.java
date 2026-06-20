import java.util.Scanner;

// 1. Прості класи виключень (прямо в цьому ж файлі)
class RegistrationException extends Exception {
    public RegistrationException(String message) { super(message); }
}

class AuthException extends Exception {
    public AuthException(String message) { super(message); }
}

// 2. Клас Користувача
class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

// 3. Головний клас програми
public class Main {
    // Наші масиви для зберігання даних (макс. 15 користувачів)
    static User[] users = new User[15];
    static int userCount = 0;

    // Масив заборонених слів для пароля
    static String[] forbiddenWords = {"admin", "pass", "password", "qwerty", "ytrewq"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- МЕНЮ ---");
            System.out.println("1 - Додати користувача");
            System.out.println("2 - Видалити користувача");
            System.out.println("3 - Виконати дію (Вхід)");
            System.out.println("4 - Додати заборонене слово");
            System.out.println("5 - Вихід");
            System.out.print("Оберіть дію: ");

            String choice = scanner.nextLine();

            // Обробляємо вибір користувача в окремих блоках try-catch, щоб програма НЕ падала
            if (choice.equals("1")) {
                try {
                    register(scanner);
                } catch (RegistrationException e) {
                    System.out.println("[ПОМИЛКА РЕЄСТРАЦІЇ]: " + e.getMessage());
                }
            } else if (choice.equals("2")) {
                try {
                    delete(scanner);
                } catch (AuthException e) {
                    System.out.println("[ПОМИЛКА ВИДАЛЕННЯ]: " + e.getMessage());
                }
            } else if (choice.equals("3")) {
                try {
                    performAction(scanner);
                } catch (AuthException e) {
                    System.out.println("[ПОМИЛКА АУТЕНТИФІКАЦІЇ]: " + e.getMessage());
                }
            } else if (choice.equals("4")) {
                addForbidden(scanner);
            } else if (choice.equals("5")) {
                System.out.println("Вихід з програми.");
                break;
            } else {
                System.out.println("Невірний пункт меню!");
            }
        }
    }

    // МЕТОД РЕЄСТРАЦІЇ
    public static void register(Scanner scanner) throws RegistrationException {
        if (userCount >= 15) {
            throw new RegistrationException("Більше користувачів додати не можна (максимум 15)!");
        }

        System.out.print("Введіть ім'я користувача: ");
        String username = scanner.nextLine();
        System.out.print("Введіть пароль: ");
        String password = scanner.nextLine();

        // Перевірка імені
        if (username.length() < 5) {
            throw new RegistrationException("Ім'я має бути не менше 5 символів.");
        }
        if (username.contains(" ")) {
            throw new RegistrationException("Ім'я не повинно містити пробілів.");
        }

        // Перевірка, чи вже є такий користувач
        for (int i = 0; i < 15; i++) {
            if (users[i] != null && users[i].username.equals(username)) {
                throw new RegistrationException("Користувач з таким ім'ям вже існує.");
            }
        }

        // Перевірка пароля за всіма критеріями
        validatePassword(password);

        // Шукаємо вільне місце в масиві та зберігаємо
        for (int i = 0; i < 15; i++) {
            if (users[i] == null) {
                users[i] = new User(username, password);
                userCount++;
                System.out.println("Успішно зареєстровано!");
                return;
            }
        }
    }

    // МЕТОД ВИДАЛЕННЯ
    public static void delete(Scanner scanner) throws AuthException {
        System.out.print("Введіть ім'я користувача для видалення: ");
        String username = scanner.nextLine();

        for (int i = 0; i < 15; i++) {
            if (users[i] != null && users[i].username.equals(username)) {
                users[i] = null; // видаляємо
                userCount--;
                System.out.println("Користувача '" + username + "' видалено.");
                return;
            }
        }
        throw new AuthException("Такого користувача не знайдено.");
    }

    // МЕТОД ВИКОНАННЯ ДІЇ (ВХІД)
    public static void performAction(Scanner scanner) throws AuthException {
        System.out.print("Ім'я: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();
        System.out.print("Введіть дію (наприклад, 'купити товар'): ");
        String action = scanner.nextLine();

        for (int i = 0; i < 15; i++) {
            if (users[i] != null && users[i].username.equals(username)) {
                if (users[i].password.equals(password)) {
                    System.out.println("[УСПІХ] Користувача було аутентифіковано.");
                    System.out.println("[ДІЯ]: Виконано дію: " + action);
                    return;
                } else {
                    throw new AuthException("Невірний пароль.");
                }
            }
        }
        throw new AuthException("Користувача з таким ім'ям не знайдено.");
    }

    // ДОДАТКОВА ФУНКЦІЯ: Валідація пароля вручну
    public static void validatePassword(String pass) throws RegistrationException {
        if (pass.length() < 10) {
            throw new RegistrationException("Пароль має бути не менше 10 символів.");
        }
        if (pass.contains(" ")) {
            throw new RegistrationException("Пароль не повинен містити пробілів.");
        }

        int digits = 0;
        int specials = 0;
        String specialCharacters = "!@#$%^&*()-_=+[]{}";

        // Посимвольний аналіз рядка (замість регулярних виразів)
        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);

            if (Character.isDigit(c)) {
                digits++;
            } else if (specialCharacters.indexOf(c) >= 0) {
                specials++;
            }
        }

        if (digits < 3) {
            throw new RegistrationException("Пароль повинен містити хоча б 3 цифри.");
        }
        if (specials < 1) {
            throw new RegistrationException("Пароль повинен містити хоча б 1 спеціальний символ.");
        }

        // Перевірка на заборонені слова
        String lowerPass = pass.toLowerCase();
        for (int i = 0; i < forbiddenWords.length; i++) {
            if (lowerPass.contains(forbiddenWords[i])) {
                throw new RegistrationException("Пароль містить заборонене слово: " + forbiddenWords[i]);
            }
        }
    }

    // ДОДАТКОВА ФУНКЦІЯ: Додавання нових заборонених слів у масив
    public static void addForbidden(Scanner scanner) {
        System.out.print("Введіть нове заборонене слово: ");
        String word = scanner.nextLine().trim().toLowerCase();

        // Створюємо новий масив, який більший на 1 елемент
        String[] newArray = new String[forbiddenWords.length + 1];

        // Копіюємо старі дані
        for (int i = 0; i < forbiddenWords.length; i++) {
            newArray[i] = forbiddenWords[i];
        }
        // Додаємо нове слово в кінець
        newArray[newArray.length - 1] = word;
        forbiddenWords = newArray;

        System.out.println("Слово '" + word + "' успішно додано до заборонених!");
    }
}