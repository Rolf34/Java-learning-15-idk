//how to use this code
// 1. Create a new Java project in your IDE.
// 2. Create a new Java class file named `App.java` in the `src` directory of your project.
// 3. Copy and paste the code below into the `App.java` file.
// 4. Save the file.
// 5. Run the `App` class to execute the program.
// 6. Follow the prompts in the console to interact with the diary application.
// 7. You can add, delete, view entries, and save/load the diary to/from a file.
// 8. The program will handle date formats and ensure that the diary is saved correctly.
// 9. You can exit the program and choose to save the diary before exiting.
// 10. Enjoy using your diary application!


import java.util.Scanner; // Importing Scanner for user input
import java.text.SimpleDateFormat; // Importing SimpleDateFormat for date formatting
import java.text.ParseException;    // Importing ParseException for date parsing errors
import java.util.Date;          // Importing Date for date representation
import java.io.File;        // Importing File for file operations
import java.io.FileWriter;             // Importing FileWriter for file writing
import java.io.BufferedReader;           // Importing BufferedReader for file reading   
import java.io.FileReader;           // Importing FileReader for file reading
import java.io.IOException;        // Importing IOException for file I/O exceptions

public class App {
    static final int MAX_ENTRIES = 50;
    static String[] dates = new String[MAX_ENTRIES]; 
    static long[] timestamps = new long[MAX_ENTRIES];
    static String[] entries = new String[MAX_ENTRIES];
    static int entryCount = 0;
    static String dateFormat = "dd.MM.yyyy";          
    static String currentFileName = null;             

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Вітаємо в додатку 'Мій щоденник'!");
        System.out.println("1. Створити новий щоденник");
        System.out.println("2. Відкрити існуючий щоденник");
        System.out.print("Ваш вибір (1-2): ");
        
        String initialChoice = scanner.nextLine();
        
        if ("2".equals(initialChoice)) {
            loadDiaryFromFile(scanner);
        } else if (!"1".equals(initialChoice)) {
            System.out.println("Невідомий вибір. Буде створено новий щоденник.");
        }
        
        chooseDateFormat(scanner);
        
        mainMenu(scanner);
    }
    
    private static void chooseDateFormat(Scanner scanner) {
        System.out.println("\nОберіть формат відображення дати:");
        System.out.println("1. DD.MM.YYYY (31.12.2025)");
        System.out.println("2. MM/DD/YYYY (12/31/2025)");
        System.out.println("3. YYYY-MM-DD (2024-12-31)");
        System.out.println("4. DD MMMM YYYY (31 грудня 2025)");
        System.out.println("5. Власний формат");
        System.out.print("Ваш вибір (1-5): ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                dateFormat = "dd.MM.yyyy";
                break;
            case "2":
                dateFormat = "MM/dd/yyyy";
                break;
            case "3":
                dateFormat = "yyyy-MM-dd";
                break;
            case "4":
                dateFormat = "dd MMMM yyyy";
                break;
            case "5":
                System.out.println("Введіть власний формат дати (приклад: dd.MM.yyyy HH:mm):");
                String customFormat = scanner.nextLine();
                if (!customFormat.isEmpty()) {
                    try {
                        // Перевірка, чи валідний формат
                        SimpleDateFormat sdf = new SimpleDateFormat(customFormat);
                        sdf.format(new Date());
                        dateFormat = customFormat;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Невалідний формат дати. Встановлено формат за замовчуванням (dd.MM.yyyy)");
                    }
                }
                break;
            default:
                System.out.println("Невідомий вибір. Встановлено формат за замовчуванням (dd.MM.yyyy)");
        }
        
        System.out.println("Встановлено формат дати: " + dateFormat);
    }

    private static void mainMenu(Scanner scanner) {
        while (true) {
            System.out.println("""
                
                Мій щоденник:
                1. Додати запис
                2. Видалити запис
                3. Переглянути всі записи
                4. Змінити формат дати
                5. Зберегти щоденник
                6. Завантажити щоденник
                7. Вийти    
                    """);
            System.out.print("Оберіть опцію (1-7): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addEntry(scanner);
                    break;
                case "2":
                    deleteEntry(scanner);
                    break;
                case "3":
                    viewAllEntries();
                    break;
                case "4":
                    chooseDateFormat(scanner);
                    break;
                case "5":
                    saveDiaryToFile(scanner);
                    break;
                case "6":
                    loadDiaryFromFile(scanner);
                    break;
                case "7":
                    confirmExit(scanner);
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private static void addEntry(Scanner scanner) {
        if (entryCount >= MAX_ENTRIES) {
            System.out.println("Щоденник повний! Видаліть старі записи.");
            return;
        }

        System.out.print("Введіть дату (формат " + dateFormat + "): ");
        String dateInput = scanner.nextLine();

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(dateFormat);
            inputFormat.setLenient(false);
            Date date = inputFormat.parse(dateInput);
            
            timestamps[entryCount] = date.getTime();
            dates[entryCount] = inputFormat.format(date);

            System.out.println("Введіть текст запису (для завершення введіть порожній рядок):");
            String entry = "";
            String line;
            while (!(line = scanner.nextLine()).isEmpty()) {
                entry += line + "\n";
            }

            if (!entry.isEmpty()) {
                entries[entryCount] = entry;
                entryCount++;
                System.out.println("Запис додано успішно!");
            }
        } catch (ParseException e) {
            System.out.println("Невірний формат дати! Будь ласка, використовуйте формат " + dateFormat);
        }
    }

    private static void deleteEntry(Scanner scanner) {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній!");
            return;
        }

        System.out.print("Введіть дату запису для видалення (формат " + dateFormat + "): ");
        String dateToDelete = scanner.nextLine();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.setLenient(false);
            sdf.parse(dateToDelete);
            
            boolean found = false;
            for (int i = 0; i < entryCount; i++) {
                if (dates[i].equals(dateToDelete)) {
                    for (int j = i; j < entryCount - 1; j++) {
                        dates[j] = dates[j + 1];
                        timestamps[j] = timestamps[j + 1];
                        entries[j] = entries[j + 1];
                    }
                    entryCount--;
                    System.out.println("Запис видалено успішно!");
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("Запис з такою датою не знайдено!");
            }
        } catch (ParseException e) {
            System.out.println("Невірний формат дати! Будь ласка, використовуйте формат " + dateFormat);
        }
    }

    private static void viewAllEntries() {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній!");
            return;
        }

        System.out.println("\nВсі записи:");
        for (int i = 0; i < entryCount; i++) {
            System.out.println("\nДата: " + dates[i]);
            System.out.println("Запис:");
            System.out.println(entries[i]);
        }
    }

    private static void saveDiaryToFile(Scanner scanner) {
        if (entryCount == 0) {
            System.out.println("Щоденник порожній, нічого зберігати!");
            return;
        }

        System.out.print("Введіть шлях до файлу для збереження" + 
                        (currentFileName != null ? " (Enter для використання " + currentFileName + ")" : "") + ": ");
        String filePath = scanner.nextLine();
        
        if (filePath.isEmpty() && currentFileName != null) {
            filePath = currentFileName;
        } else if (filePath.isEmpty()) {
            System.out.println("Шлях до файлу не вказано!");
            return;
        }

        try {
            FileWriter writer = new FileWriter(filePath);
            
            writer.write("FORMAT:" + dateFormat + "\n");
            
            for (int i = 0; i < entryCount; i++) {
                writer.write("DATE:" + timestamps[i] + "\n");
                writer.write("ENTRY:\n" + entries[i]);
                writer.write("END_ENTRY\n\n");
            }
            
            writer.close();
            currentFileName = filePath;
            System.out.println("Щоденник успішно збережено у файл: " + filePath);
            
        } catch (IOException e) {
            System.out.println("Помилка при збереженні файлу: " + e.getMessage());
        }
    }

    private static void loadDiaryFromFile(Scanner scanner) {
        System.out.print("Введіть шлях до файлу для завантаження: ");
        String filePath = scanner.nextLine();
        
        if (filePath.isEmpty()) {
            System.out.println("Шлях до файлу не вказано!");
            return;
        }
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.out.println("Файл не знайдено або це не файл!");
            return;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            entryCount = 0;
            
            String line = reader.readLine();
            
            if (line != null && line.startsWith("FORMAT:")) {
                String savedFormat = line.substring(7);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(savedFormat);
                    sdf.format(new Date());
                    dateFormat = savedFormat;
                    System.out.println("Завантажено формат дати: " + dateFormat);
                } catch (IllegalArgumentException e) {
                    System.out.println("Невалідний формат дати у файлі. Використовується формат за замовчуванням.");
                }
                
                line = reader.readLine();
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            String currentEntry = "";
            long currentTimestamp = 0;
                        while (line != null) {
                if (line.startsWith("DATE:")) {
                    try {
                        currentTimestamp = Long.parseLong(line.substring(5));
                    } catch (NumberFormatException e) {
                        System.out.println("Помилка при зчитуванні дати із файлу. Пропускаємо запис.");
                        // Перейти до наступного запису
                        while (line != null && !line.equals("END_ENTRY")) {
                            line = reader.readLine();
                        }
                        line = reader.readLine(); // пропускаємо порожній рядок
                        continue;
                    }
                } else if (line.equals("ENTRY:")) {
                    currentEntry = "";
                    line = reader.readLine();
                    
                    while (line != null && !line.equals("END_ENTRY")) {
                        currentEntry += line + "\n";
                        line = reader.readLine();
                    }
                    
                    if (entryCount < MAX_ENTRIES) {
                        timestamps[entryCount] = currentTimestamp;
                        Date entryDate = new Date(currentTimestamp);
                        dates[entryCount] = sdf.format(entryDate);
                        entries[entryCount] = currentEntry;
                        entryCount++;
                    } else {
                        System.out.println("Досягнуто максимальну кількість записів. Деякі записи не завантажено.");
                        break;
                    }
                }
                
                line = reader.readLine();
            }
            
            reader.close();
            currentFileName = filePath;
            System.out.println("Щоденник успішно завантажено з файлу: " + filePath);
            System.out.println("Завантажено " + entryCount + " записів.");
            
        } catch (IOException e) {
            System.out.println("Помилка при зчитуванні файлу: " + e.getMessage());
        }
    }

    private static void confirmExit(Scanner scanner) {
        if (entryCount > 0) {
            System.out.print("Зберегти щоденник перед виходом? (так/ні): ");
            String response = scanner.nextLine().toLowerCase();
            
            if (response.equals("так") || response.equals("y") || response.equals("yes")) {
                saveDiaryToFile(scanner);
            }
        }
        
        System.out.println("Дякуємо за використання програми 'Мій щоденник'. До побачення!");
        scanner.close();
    }
}

// P.s ya reshil pisat podobniye comments, eto voobshe udobno??
