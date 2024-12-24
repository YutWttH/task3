import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjReader {

    private List<float[]> vertices;
    private List<int[]> faces;

    public ObjReader() {
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
    }

    public void read(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line.trim());
            }

            validateData();
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении файла: " + e.getMessage(), e);
        }
    }

    private void parseLine(String line) {
        if (line.startsWith("#") || line.isEmpty()) {
            // Это комментарий или пустая строка
            return;
        }

        String[] parts = line.split("\\s+");
        switch (parts[0]) {
            case "v":
                parseVertex(parts);
                break;
            case "f":
                parseFace(parts);
                break;
            default:
                // Обработка других частей файла или игнорирование
                break;
        }
    }

    private void parseVertex(String[] parts) {
        try {
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            float z = Float.parseFloat(parts[3]);
            vertices.add(new float[]{x, y, z});
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Некорректные данные вершины: " + e.getMessage());
        }
    }

    private void parseFace(String[] parts) {
        try {
            int[] vertexIndices = new int[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                String[] components = parts[i].split("/");
                int index = Integer.parseInt(components[0]);
                if (index < 0) {
                    index = vertices.size() + index + 1; // обработка относительных индексов
                }
                vertexIndices[i - 1] = index - 1; // индексы в Obj-файле начинаются с 1
            }
            for (int index : vertexIndices) {
                if (index >= vertices.size() || index < 0) {
                    throw new IndexOutOfBoundsException("Индекс вершины вне диапазона: " + index);
                }
            }
            faces.add(vertexIndices);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Некорректные данные полигона: " + e.getMessage());
        }
    }

    private void validateData() {
        if (vertices.isEmpty()) {
            throw new IllegalStateException("Модель не содержит вершин.");
        }
        if (faces.isEmpty()) {
            throw new IllegalStateException("Модель не содержит полигонов.");
        }
    }

    public List<float[]> getVertices() {
        return vertices;
    }

    public List<int[]> getFaces() {
        return faces;
    }

    public static void main(String[] args) {
        ObjReader objReader = new ObjReader();
        try {
            objReader.read("src\\Cube2.obj");
            System.out.println("Модель успешно загружена.");
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}