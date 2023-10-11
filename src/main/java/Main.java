import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Translation translation = new Translation();

        // 创建及设置窗口
        JFrame frame = new JFrame("翻译助手");
        URL url = Main.class.getResource("Translation.png");
        if (url != null) {
            frame.setIconImage(new ImageIcon(url).getImage());
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //配置面板
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
        jPanel.add(new JLabel("源文件："));
        JTextField srcEdit = new JTextField("", 20);
        jPanel.add(srcEdit);
        JButton srcButton = new JButton("选择");
        srcButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                srcEdit.setText(chooser.getSelectedFile().getPath());
            }
        });
        jPanel.add(srcButton);
        jPanel.add(new JLabel("翻译源1："));
        JTextField translation1Edit = new JTextField("", 20);
        jPanel.add(translation1Edit);
        JButton translation1Button = new JButton("选择");
        translation1Button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                translation1Edit.setText(chooser.getSelectedFile().getPath());
            }
        });
        jPanel.add(translation1Button);
        jPanel.add(new JLabel("翻译源2："));
        JTextField translation2Edit = new JTextField("", 20);
        jPanel.add(translation2Edit);
        JButton translation2Button = new JButton("选择");
        translation2Button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                translation2Edit.setText(chooser.getSelectedFile().getPath());
            }
        });
            jPanel.add(translation2Button);
        JCheckBox jCheckBox1 = new JCheckBox("只显示翻译");
        JCheckBox jCheckBox2 = new JCheckBox("只显示未翻译");
        jCheckBox1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (jCheckBox1.isSelected()) {
                    jCheckBox2.setSelected(false);
                }
            }
        });
        jCheckBox2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (jCheckBox2.isSelected()) {
                    jCheckBox1.setSelected(false);
                }
            }
        });
        jPanel.add(jCheckBox1);
        jPanel.add(jCheckBox2);
        JButton startUpButton = new JButton("解析");
        jPanel.add(startUpButton);
        JButton saveButton = new JButton("另存为");
        jPanel.add(saveButton);
        frame.getContentPane().add(jPanel, BorderLayout.NORTH);

        //接收内容
        JTable jTable = new JTable();
        frame.getContentPane().add(new JScrollPane(jTable), BorderLayout.CENTER);

        // 显示窗口
        frame.pack();
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        startUpButton.addActionListener(e -> {
            List<String[]> key2value = translation.translation(srcEdit.getText(), Translation.getKey2value(translation1Edit.getText()), Translation.getKey2value(translation2Edit.getText()), jCheckBox1.isSelected() ? 1 : jCheckBox2.isSelected() ? 2 : 0);
            String[] columnNames = {"序号", "key", "value", "翻译", "翻译1", "翻译2"};//定义表格列
            String[][] tableValues = new String[key2value.size()][columnNames.length];//定义数字，用来存储表格数据
            for (int i = 0; i < key2value.size(); i++) {
                tableValues[i][0] = String.valueOf(i);
                tableValues[i][1] = key2value.get(i)[0];
                tableValues[i][2] = key2value.get(i)[1];
                tableValues[i][3] = key2value.get(i)[2];
                tableValues[i][4] = key2value.get(i)[3];
                tableValues[i][5] = key2value.get(i)[4];
            }
            DefaultTableModel defaultTableModel = (DefaultTableModel) jTable.getModel();
            defaultTableModel.setDataVector(tableValues, columnNames);
        });
        saveButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file.isFile()) {
                    if (JOptionPane.showConfirmDialog(frame, String.format("是否覆盖文件%s？", file.getPath()), "提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (file.delete()) {
                            translation.saveTo(file.getPath());
                        }
                    }
                } else if (file.isDirectory()) {
                    String srcPath = srcEdit.getText();
                    if (srcPath != null && !srcPath.isEmpty()) {
                        File file2 = new File(file, new File(srcPath).getName());
                        if (file2.isFile()) {
                            if (JOptionPane.showConfirmDialog(frame, String.format("是否覆盖文件%s？", file2.getPath()), "提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                if (file2.delete()) {
                                    translation.saveTo(file2.getPath());
                                }
                            }
                        } else if (file2.isDirectory()) {
                            JOptionPane.showMessageDialog(frame, String.format("%s是一个目录", file2.getPath()), "提示", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            translation.saveTo(file2.getPath());
                        }
                    }
                } else {
                    translation.saveTo(file.getPath());
                }
            }
        });
    }

    static class Translation {

        SAXReader reader = new SAXReader();
        public Document document = null;

        public Translation() {
        }

        public void saveTo(String destPath) {
            if (document != null) {
                try {
                    //创建输出格式
                    OutputFormat format = OutputFormat.createPrettyPrint();
                    //XML写入工具
                    XMLWriter writer = new XMLWriter(new FileOutputStream(destPath), format);
                    //写入文档至XML文件
                    writer.write(document);
                    //关闭流
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * @param filePath
         * @param translation1
         * @param translation2
         * @param model        0默认返回全部，1只返回有翻译的，2只返回未翻译的
         * @return
         */
        public List<String[]> translation(String filePath, Map<String, String> translation1, Map<String, String> translation2, int model) {
            List<String[]> key2value = new ArrayList<>();
            try {
                document = reader.read(new File(filePath));
                System.out.println(String.format("getXMLEncoding=%s", document.getXMLEncoding()));
                Element rootElement = document.getRootElement();
                List<Element> rootChildren = rootElement.elements();
                List<Element> removeChildren = new ArrayList<>();
                for (int i = 0; i < rootChildren.size(); i++) {
                    Element child = rootChildren.get(i);
                    String key = child.attributeValue("name");
                    String value = child.getStringValue();
                    String translation = translation2.getOrDefault(key, translation1.getOrDefault(key, ""));
                    if (!translation.isEmpty()) {
                        child.setText(translation);
                    }
                    if (model == 1) {
                        if (!translation.isEmpty()) {
                            key2value.add(new String[]{key, value, translation,
                                    translation1.getOrDefault(key, ""),
                                    translation2.getOrDefault(key, "")
                            });
                        } else {
                            removeChildren.add(child);
                        }
                    } else if (model == 2) {
                        if (translation.isEmpty()) {
                            key2value.add(new String[]{key, value, translation,
                                    translation1.getOrDefault(key, ""),
                                    translation2.getOrDefault(key, "")
                            });
                        } else {
                            removeChildren.add(child);
                        }
                    } else if (model == 0) {
                        key2value.add(new String[]{key, value, translation,
                                translation1.getOrDefault(key, ""),
                                translation2.getOrDefault(key, "")
                        });
                    }
                    System.out.println(String.format("%d [%s]=%s type=%s", i, key, value, child.getName()));
                }
                for (Element child : removeChildren) {
                    rootElement.remove(child);
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            return key2value;
        }

        public static Map<String, String> getKey2value(String filePath) {
            Map<String, String> key2value = new HashMap<>();
            if (filePath != null && !filePath.trim().isEmpty()) {
                try {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(new File(filePath));
                    Element rootElement = document.getRootElement();
                    List<Element> rootChildren = rootElement.elements();
                    for (int i = 0; i < rootChildren.size(); i++) {
                        Element child = rootChildren.get(i);
                        System.out.println(String.format("%d [%s]=%s type=%s", i, child.attributeValue("name"), child.getStringValue(), child.getName()));
                        key2value.put(child.attributeValue("name"), child.getStringValue());
                    }
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
            return key2value;
        }

    }

}
