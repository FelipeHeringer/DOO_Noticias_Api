package com.fag.doo_noticias_api.gui;

import javax.swing.*;

import com.fag.doo_noticias_api.MongoConnection;
import com.fag.doo_noticias_api.NewsApiClient;
import com.fag.doo_noticias_api.models.News;
import com.fag.doo_noticias_api.models.SearchHistory;
import com.fag.doo_noticias_api.models.User;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NewsAppGUI {
    // Componentes principais
    private JFrame mainFrame;
    private JFrame loginFrame;
    private User currentUser;
    private NewsApiClient newsApiClient;
    private MongoConnection mongoConnection;
    private SearchHistory searchHistory;

    // Componentes da interface principal
    private JTextArea resultTextArea;
    private JTextArea favoritesHistoryTextArea;
    private JComboBox<String> listSelector;
    private JComboBox<String> sortSelector;

    // Campos de busca
    private JTextField titleSearchField;
    private JTextField keywordSearchField;
    private JTextField dateSearchField;

    public NewsAppGUI() {
        // Iniciar com a configuração do usuário
        newsApiClient = new NewsApiClient();
        mongoConnection = new MongoConnection();
        searchHistory = new SearchHistory();
        showLoginScreen();
        resultTextArea = new JTextArea();
    }

    private void showLoginScreen() {
        loginFrame = new JFrame("Noticias App - Login");
        loginFrame.setSize(400, 300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Bem-Vindo à API de Notícias");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setMaximumSize(new Dimension(400, 50));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Cadastro");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(buttonPanel);

        loginFrame.add(loginPanel);

        // Ação para botão de login
        loginButton.addActionListener(e -> showLoginForm());

        // Ação para botão de cadastro
        registerButton.addActionListener(e -> showRegisterForm());

        loginFrame.setVisible(true);
    }

    private void showLoginForm() {
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
        loginFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel cpfLabel = new JLabel("Informe seu CPF:");
        JTextField cpfField = new JTextField(15);
        cpfField.setMaximumSize(new Dimension(Integer.MAX_VALUE, cpfField.getPreferredSize().height));

        JButton submitButton = new JButton("Entrar");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginFormPanel.add(cpfLabel);
        loginFormPanel.add(Box.createVerticalStrut(5));
        loginFormPanel.add(cpfField);
        loginFormPanel.add(Box.createVerticalStrut(20));
        loginFormPanel.add(submitButton);

        // Limpa o frame e adiciona o formulário
        loginFrame.getContentPane().removeAll();
        loginFrame.add(loginFormPanel);
        loginFrame.revalidate();
        loginFrame.repaint();

        // Ação para o botão de submit
        submitButton.addActionListener(e -> {
            String cpf = cpfField.getText().trim();

            // Validar CPF
            if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
                JOptionPane.showMessageDialog(loginFrame,
                        "O CPF deve estar no formato XXX.XXX.XXX-XX",
                        "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar usuário no MongoDB
            User user = mongoConnection.getUserFromMongoByCpf(cpf);

            if (user != null) {
                currentUser = user;
                JOptionPane.showMessageDialog(loginFrame,
                        "Login realizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.dispose();
                showMainScreen();
            } else {
                JOptionPane.showMessageDialog(loginFrame,
                        "Usuário não encontrado. Verifique o CPF informado.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                cpfField.setText("");
            }
        });
    }

    private void showRegisterForm() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Nome ou Apelido:");
        JTextField nameField = new JTextField(15);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));

        JLabel cpfLabel = new JLabel("CPF (XXX.XXX.XXX-XX):");
        JTextField cpfField = new JTextField(15);
        cpfField.setMaximumSize(new Dimension(Integer.MAX_VALUE, cpfField.getPreferredSize().height));

        JButton submitButton = new JButton("Cadastrar");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerPanel.add(nameLabel);
        registerPanel.add(Box.createVerticalStrut(5));
        registerPanel.add(nameField);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(cpfLabel);
        registerPanel.add(Box.createVerticalStrut(5));
        registerPanel.add(cpfField);
        registerPanel.add(Box.createVerticalStrut(20));
        registerPanel.add(submitButton);

        // Limpa o frame e adiciona o formulário
        loginFrame.getContentPane().removeAll();
        loginFrame.add(registerPanel);
        loginFrame.revalidate();
        loginFrame.repaint();

        // Ação para o botão de cadastro
        submitButton.addActionListener(e -> {
            String nome = nameField.getText().trim();
            String cpf = cpfField.getText().trim();

            // Validações
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame,
                        "O nome não pode estar vazio",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
                JOptionPane.showMessageDialog(loginFrame,
                        "O CPF deve estar no formato XXX.XXX.XXX-XX",
                        "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                cpfField.setText("");
                return;
            }

            // Criar novo usuário
            User newUser = new User(nome, cpf);
            currentUser = newUser;

            // Salvar no MongoDB
            Map<String, Object> userJsonRegisterInformation = newUser.toJson();
            mongoConnection.putUserOnMongo(userJsonRegisterInformation);

            JOptionPane.showMessageDialog(loginFrame,
                    "Cadastro realizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            loginFrame.dispose();
            showMainScreen();
        });
    }

    private void showMainScreen() {
        mainFrame = new JFrame("Sistema de Notícias IBGE - " + currentUser.getName());
        mainFrame.setSize(1200, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);

        // Layout principal com 2 colunas
        mainFrame.setLayout(new GridLayout(1, 2, 10, 10));

        // Painel esquerdo (controles)
        JPanel leftPanel = createLeftPanel();

        // Painel direito (área de resultados)
        JPanel rightPanel = createRightPanel();

        mainFrame.add(leftPanel);
        mainFrame.add(rightPanel);

        mainFrame.setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Seção de informações do usuário
        JPanel userPanel = createUserInfoPanel();

        // Seção de pesquisa de notícias
        JPanel searchPanel = createSearchPanel();

        // Seção de gerenciar notícias (adicionar nas listas)
        JPanel addNewsPanel = createAddNewsPanel();

        // Seção de gerenciar notícias (remover das listas)
        JPanel removeNewsPanel = createRemoveNewsPanel();

        // Seção de visualização de listas
        JPanel viewListPanel = createViewListPanel();

        // Seção de histórico/favoritos
        JPanel historyPanel = createHistoryPanel();

        // Adicionar todos os painéis
        panel.add(userPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(searchPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(addNewsPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(removeNewsPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewListPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(historyPanel);

        return panel;
    }

    private JPanel createUserInfoPanel() {
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createTitledBorder("Informações do Usuário"));

        JLabel userNameLabel = new JLabel("Usuário: " + currentUser.getName());
        JLabel dateLabel = new JLabel("Data: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        JButton editNameButton = new JButton("Alterar Nome");
        editNameButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        userPanel.add(userNameLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(dateLabel);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(editNameButton);

        // Listener para editar nome
        editNameButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(mainFrame,
                    "Digite o novo nome:",
                    currentUser.getName());

            if (newName != null && !newName.trim().isEmpty()) {
                currentUser.setName(newName.trim());
                mongoConnection.updateUserNameByCpf(currentUser.getCpf(), newName.trim());
                userNameLabel.setText("Usuário: " + currentUser.getName());
                mainFrame.setTitle("Sistema de Notícias IBGE - " + currentUser.getName());
                JOptionPane.showMessageDialog(mainFrame,
                        "Nome atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return userPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar Notícias"));

        // Busca por título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Título:");
        titleSearchField = new JTextField(15);
        JButton searchByTitleButton = new JButton("Buscar");

        titlePanel.add(titleLabel);
        titlePanel.add(titleSearchField);
        titlePanel.add(searchByTitleButton);

        // Busca por palavras-chave
        JPanel keywordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel keywordLabel = new JLabel("Palavras-chave:");
        keywordSearchField = new JTextField(15);
        JButton searchByKeywordButton = new JButton("Buscar");

        keywordPanel.add(keywordLabel);
        keywordPanel.add(keywordSearchField);
        keywordPanel.add(searchByKeywordButton);

        // Busca por data
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dateLabel = new JLabel("Data (dd/MM/yyyy):");
        dateSearchField = new JTextField(10);
        JButton searchByDateButton = new JButton("Buscar");

        datePanel.add(dateLabel);
        datePanel.add(dateSearchField);
        datePanel.add(searchByDateButton);

        searchPanel.add(titlePanel);
        searchPanel.add(keywordPanel);
        searchPanel.add(datePanel);
        searchPanel.add(Box.createVerticalStrut(10));

        // Listeners dos botões de busca
        searchByTitleButton.addActionListener(e -> {
            String title = titleSearchField.getText().trim();
            if (!title.isEmpty()) {
                searchNewsByTitle(title);
            } else {
                showWarning("Por favor, digite um título para buscar");
            }
        });

        searchByKeywordButton.addActionListener(e -> {
            String keywords = keywordSearchField.getText().trim();
            if (!keywords.isEmpty()) {
                searchNewsByKeywords(keywords);
            } else {
                showWarning("Por favor, digite palavras-chave para buscar");
            }
        });

        searchByDateButton.addActionListener(e -> {
            String date = dateSearchField.getText().trim();
            if (date.isEmpty()) {
                showWarning("Por favor, digite uma data para buscar");
            }

            if (isValidDate(date)) {
                searchNewsByDate(date);
            } else {
                showError("Data inválida. Use o formato dd/MM/yyyy");
            }

        });

        return searchPanel;
    }

    private JPanel createAddNewsPanel() {
        JPanel addToListPanel = new JPanel();
        addToListPanel.setLayout(new BoxLayout(addToListPanel, BoxLayout.Y_AXIS));
        addToListPanel.setBorder(BorderFactory.createTitledBorder("Adicionar Notícia à Lista"));

        JPanel newsIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel newsIdLabel = new JLabel("ID da Série:");
        JTextField newsIdField = new JTextField(5);

        newsIdPanel.add(newsIdLabel);
        newsIdPanel.add(newsIdField);

        JPanel listTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel listTypeLabel = new JLabel("Lista:");
        String[] listTypes = { "Notícias Favoritas", "Notícias para ler depois" };
        JComboBox<String> listTypeCombo = new JComboBox<>(listTypes);

        listTypePanel.add(listTypeLabel);
        listTypePanel.add(listTypeCombo);

        JButton addToListButton = new JButton("Adicionar à Lista");
        addToListButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        addToListPanel.add(newsIdPanel);
        addToListPanel.add(listTypePanel);
        addToListPanel.add(Box.createVerticalStrut(5));
        addToListPanel.add(addToListButton);

        addToListButton.addActionListener(e -> {
            String id = newsIdField.getText().trim();
            if (validateNewsId(id)) {
                News news = searchHistory.searchNewsById(Integer.parseInt(id));
                if (news == null) {
                    showWarning("Notícia não encontrada. Verifique o ID ou faça uma busca primeiro.");
                }
                int selectedListIndex = listTypeCombo.getSelectedIndex();
                switch (selectedListIndex) {
                    case 0: // Favoritas
                        addToFavorites(news);
                        break;
                    case 1: // Para ler depois
                        addToReadLater(news);
                        break;
                }
            }
        });

        return addToListPanel;

    }

    private JPanel createRemoveNewsPanel() {
        JPanel removeToListPanel = new JPanel();
        removeToListPanel.setLayout(new BoxLayout(removeToListPanel, BoxLayout.Y_AXIS));
        removeToListPanel.setBorder(BorderFactory.createTitledBorder("Remover Notícia da Lista"));

        JPanel newsIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel newsIdLabel = new JLabel("ID da Série:");
        JTextField newsIdField = new JTextField(5);

        newsIdPanel.add(newsIdLabel);
        newsIdPanel.add(newsIdField);

        JPanel listTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel listTypeLabel = new JLabel("Lista:");
        String[] listTypes = { "Notícias Favoritas", "Notícias para ler depois", "Notícias Lidas" };
        JComboBox<String> listTypeCombo = new JComboBox<>(listTypes);

        listTypePanel.add(listTypeLabel);
        listTypePanel.add(listTypeCombo);

        JButton removeToListButton = new JButton("Remover da Lista");
        removeToListButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        removeToListPanel.add(newsIdPanel);
        removeToListPanel.add(listTypePanel);
        removeToListPanel.add(Box.createVerticalStrut(5));
        removeToListPanel.add(removeToListButton);

        removeToListButton.addActionListener(e -> {
            String id = newsIdField.getText().trim();
            if (validateNewsId(id)) {
                int selectedListIndex = listTypeCombo.getSelectedIndex();
                switch (selectedListIndex) {
                    case 0: // Favoritas
                        News news = currentUser.getFavoriteNews().stream()
                                .filter(n -> n.getId() == Integer.parseInt(id))
                                .findFirst()
                                .orElse(null);
                        if (news == null) {
                            showWarning(
                                    "Notícia não encontrada nos favoritos. Verifique o ID ou faça uma busca primeiro.");
                            return;
                        }
                        removeFromFavorites(news);
                        break;
                    case 1: // Para ler depois
                        News newsToReadLater = currentUser.getNewsToReadLater().stream()
                                .filter(n -> n.getId() == Integer.parseInt(id))
                                .findFirst()
                                .orElse(null);
                        if (newsToReadLater == null) {
                            showWarning(
                                    "Notícia não encontrada nos favoritos. Verifique o ID ou faça uma busca primeiro.");
                            return;
                        }
                        removeFromReadLater(newsToReadLater);
                        break;
                    case 2: // Lidas
                        News newsRead = currentUser.getNewsRead().stream()
                                .filter(n -> n.getId() == Integer.parseInt(id))
                                .findFirst()
                                .orElse(null);
                        if (newsRead == null) {
                            showWarning(
                                    "Notícia não encontrada nos favoritos. Verifique o ID ou faça uma busca primeiro.");
                            return;
                        }
                        removeFromRead(newsRead);
                        break;
                }
            }
        });

        return removeToListPanel;
    }

    private JPanel createViewListPanel() {
        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
        viewPanel.setBorder(BorderFactory.createTitledBorder("Visualizar Listas"));

        // Seletor de lista
        JPanel listSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel listLabel = new JLabel("Selecionar Lista:");
        String[] listTypes = {
                "Notícias Favoritas",
                "Notícias Lidas",
                "Notícias para Ler Depois"
        };
        listSelector = new JComboBox<>(listTypes);

        listSelectorPanel.add(listLabel);
        listSelectorPanel.add(listSelector);

        // Seletor de ordenação
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sortLabel = new JLabel("Ordenar por:");
        String[] sortOptions = {
                "Ordem Alfabética (Título)",
                "Data de Publicação (Mais Recente)",
                "Data de Publicação (Mais Antiga)",
                "Categoria/Tipo"
        };
        sortSelector = new JComboBox<>(sortOptions);

        sortPanel.add(sortLabel);
        sortPanel.add(sortSelector);

        // Botão para visualizar
        JButton viewListButton = new JButton("Visualizar Lista");
        viewListButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        viewPanel.add(listSelectorPanel);
        viewPanel.add(sortPanel);
        viewPanel.add(Box.createVerticalStrut(10));
        viewPanel.add(viewListButton);

        // Listener do botão
        viewListButton.addActionListener(e -> {
            List<News> listToShow = null;

            switch (listSelector.getSelectedIndex()) {
                case 0: // Favoritas
                    listToShow = currentUser.getFavoriteNews();
                    break;
                case 1: // Lidas
                    listToShow = currentUser.getNewsRead();
                    break;
                case 2: // Para ler depois
                    listToShow = currentUser.getNewsToReadLater();
                    break;
            }

            if (listToShow == null || listToShow.isEmpty()) {
                resultTextArea.setText("A lista " + listSelector.getSelectedItem() + " está vazia.");
                return;
            }

            switch (sortSelector.getSelectedIndex()) {
                case 0: // Ordem Alfabética (Título)
                    Collections.sort(listToShow, Comparator.comparing(News::getTitle));
                    break;
                case 1: // Data de Publicação (Mais Recente)
                    Collections.sort(listToShow, Comparator.comparing(News::getPublicationDate).reversed());
                    break;
                case 2: // Data de Publicação (Mais Antiga)
                    Collections.sort(listToShow, Comparator.comparing(News::getPublicationDate));
                    break;
                case 3: // Categoria/Tipo
                    Collections.sort(listToShow, Comparator.comparing(News::getType));
                    break;
            }
            String selectedList = (String) listSelector.getSelectedItem();
            String selectedSort = (String) sortSelector.getSelectedItem();
            showSelectedList(selectedList, selectedSort, listToShow);
        });

        return viewPanel;
    }

    private JPanel createHistoryPanel() {
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Resumo das Listas"));

        favoritesHistoryTextArea = new JTextArea(6, 20);
        favoritesHistoryTextArea.setEditable(false);
        favoritesHistoryTextArea
                .setText("Favoritos: " + currentUser.getFavoriteNews().size() + "\nLidas: "
                        + currentUser.getNewsRead().size() + "\nPara ler depois: "
                        + currentUser.getNewsToReadLater().size() + "\n\nÚltimas ações realizadas aparecerão aqui...");

        JScrollPane historyScrollPane = new JScrollPane(favoritesHistoryTextArea);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        return historyPanel;
    }

    // Replace the createRightPanel method with this enhanced version
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título do painel de resultados
        JLabel resultsTitle = new JLabel("Resultados das Buscas e Listas");
        resultsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(resultsTitle, BorderLayout.NORTH);

        // Container for news results with scroll
        JPanel newsContainer = new JPanel();
        newsContainer.setLayout(new BoxLayout(newsContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(newsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Store reference to update content
        this.newsContainer = newsContainer;

        // Initial welcome message
        displayWelcomeMessage();

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Add this field to your class
    private JPanel newsContainer;

    // Method to display welcome message
    private void displayWelcomeMessage() {
        newsContainer.removeAll();

        JTextArea welcomeText = new JTextArea();
        welcomeText.setEditable(false);
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);
        welcomeText.setBackground(newsContainer.getBackground());
        welcomeText.setText("Bem-vindo ao Sistema de Notícias IBGE!\n\n" +
                "Use os controles à esquerda para:\n" +
                "• Buscar notícias por título, palavras-chave ou data\n" +
                "• Adicionar notícias aos favoritos\n" +
                "• Marcar notícias como lidas\n" +
                "• Criar lista para ler depois\n" +
                "• Visualizar e ordenar suas listas\n\n" +
                "Os resultados aparecerão nesta área.");

        newsContainer.add(welcomeText);
        newsContainer.revalidate();
        newsContainer.repaint();
    }

    // Method to display news list with buttons
    private void displayNewsList(List<News> newsList, String searchTerm) {
        newsContainer.removeAll();

        if (newsList.isEmpty()) {
            JLabel noResultsLabel = new JLabel("Nenhuma notícia encontrada para: \"" + searchTerm + "\"");
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            newsContainer.add(noResultsLabel);
        } else {
            JLabel headerLabel = new JLabel("Séries encontradas para \"" + searchTerm + "\":");
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            newsContainer.add(headerLabel);
            newsContainer.add(Box.createVerticalStrut(10));

            for (News news : newsList) {
                JPanel newsPanel = createNewsPanel(news);
                newsContainer.add(newsPanel);
                newsContainer.add(Box.createVerticalStrut(10));
            }
        }

        newsContainer.revalidate();
        newsContainer.repaint();
    }

    // Create individual news panel with buttons
    private JPanel createNewsPanel(News news) {
        JPanel newsPanel = new JPanel();
        newsPanel.setLayout(new BorderLayout());
        newsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // News information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        // News details
        JLabel idLabel = new JLabel("ID: " + news.getId());
        idLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel titleLabel = new JLabel("Título: " + news.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel introLabel = new JLabel("Introdução: " + news.getIntroduction());

        JLabel dateLabel = new JLabel("Data de Publicação: " + news.getPublicationDate());
        JLabel typeLabel = new JLabel("Tipo: " + news.getType());
        JLabel editorialLabel = new JLabel("Editorial: " + news.getNewsEditorial());

        // Link as clickable (optional)
        JLabel linkLabel = new JLabel("<html><a href=''>" + news.getFullNewsLink() + "</a></html>");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(introLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(typeLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(editorialLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(linkLabel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Create buttons
        JButton markReadButton = new JButton("Marcar como Lida");

        // Set button sizes
        Dimension buttonSize = new Dimension(150, 25);
        markReadButton.setPreferredSize(buttonSize);
        markReadButton.setMaximumSize(buttonSize);

        // Add action listeners
        markReadButton.addActionListener(e -> {
            markAsRead(news);
            markReadButton.setText("✓ Lida");
            markReadButton.setEnabled(false);
            markReadButton.setBackground(Color.GREEN);
        });

        buttonsPanel.add(markReadButton);
        buttonsPanel.add(Box.createVerticalStrut(5));
        buttonsPanel.add(Box.createVerticalGlue()); // Push buttons to top

        newsPanel.add(infoPanel, BorderLayout.CENTER);
        newsPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return newsPanel;
    }

    // Update your search methods to use the new display method
    private void searchNewsByTitle(String title) {
        try {
            List<News> newsList = newsApiClient.searchNewsByTitleOrKeyWord(title);
            displayNewsList(newsList, title);
            searchHistory.addSearchOnHistory(title, newsList);
        } catch (Exception e) {
            showError("Erro ao buscar notícias: " + e.getMessage());
        }
    }

    private void searchNewsByKeywords(String keywords) {
        try {
            List<News> newsList = newsApiClient.searchNewsByTitleOrKeyWord(keywords);
            displayNewsList(newsList, keywords);
            searchHistory.addSearchOnHistory(keywords, newsList);
        } catch (Exception e) {
            showError("Erro ao buscar notícias: " + e.getMessage());
        }
    }

    private void searchNewsByDate(String date) {
        try {
            List<News> newsList = newsApiClient.searchNewsByDate(date);
            displayNewsList(newsList, date);
            searchHistory.addSearchOnHistory(date, newsList);
        } catch (Exception e) {
            showError("Erro ao buscar notícias: " + e.getMessage());
        }
    }

    // Métodos de validação e utilitários
    private boolean isValidDate(String date) {
        try {
            String[] parts = date.split("/");
            if (parts.length != 3)
                return false;

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            return day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 1900 && year <= 2030;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateNewsId(String id) {
        if (id.isEmpty()) {
            showWarning("Por favor, digite o ID da notícia");
            return false;
        }

        try {
            int newsId = Integer.parseInt(id);
            if (newsId <= 0) {
                showError("O ID deve ser um número positivo");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showError("O ID deve ser um número válido");
            return false;
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addToFavorites(News news) {
        showSuccess("Notícia ID " + news.getId() + " adicionada aos favoritos!");
        updateHistoryArea("Adicionado aos favoritos: ID " + news.getId());
        currentUser.addNewsOnfavoriteList(news);
        mongoConnection.updateFavoriteNewsList(currentUser.getCpf(), news);

    }

    private void removeFromFavorites(News news) {
        showSuccess("Notícia ID " + news.getId() + " removida dos favoritos!");
        updateHistoryArea("Removido dos favoritos: ID " + news.getId());
        currentUser.removeNewsFromFavoriteList(news);
        mongoConnection.removeNewsFromFavoriteList(currentUser.getCpf(), news);

    }

    private void markAsRead(News news) {
        showSuccess("Notícia ID " + news.getId() + " marcada como lida!");
        updateHistoryArea("Marcado como lida: ID " + news.getId());
        currentUser.addNewsOnNewsRead(news);
        mongoConnection.updateNewsReadList(currentUser.getCpf(), news);
    }

    private void removeFromRead(News news) {
        showSuccess("Notícia ID " + news.getId() + " removida das lidas!");
        updateHistoryArea("Removido das lidas: ID " + news.getId());
        currentUser.removeNewsFromNewsReadList(news);
        mongoConnection.removeNewsFromReadList(currentUser.getCpf(), news);
    }

    private void addToReadLater(News news) {
        showSuccess("Notícia ID " + news.getId() + " adicionada para ler depois!");
        updateHistoryArea("Adicionado para ler depois: ID " + news.getId());
        currentUser.addNewsOnNewsToReadLaterList(news);
        mongoConnection.updateNewsToReadLaterList(currentUser.getCpf(), news);
    }

    private void removeFromReadLater(News news) {
        showSuccess("Notícia ID " + news.getId() + " removida de ler depois!");
        updateHistoryArea("Removido de ler depois: ID " + news.getId());
        currentUser.removeNewsFromNewsToReadLaterList(news);
        mongoConnection.removeNewsFromReadLaterList(currentUser.getCpf(), news);
    }

    private void showSelectedList(String listType, String sortType, List<News> listToShow) {
        newsContainer.removeAll();
        StringBuilder result = new StringBuilder();
        result.append(listType).append(" (Ordenada por ")
                .append(sortType).append("):\n\n");

        for (News news : listToShow) {
            result.append("ID: ").append(news.getId()).append("\n");
            result.append("Titulo: ").append(news.getTitle()).append("\n");
            result.append("Introdução: ").append(news.getIntroduction()).append("\n");
            result.append("Data de Publicação: ").append(news.getPublicationDate()).append("\n");
            result.append("Link completo da notícia: ").append(news.getFullNewsLink()).append("\n");
            result.append("Tipo(categoria): ").append(news.getType()).append("\n");
            result.append("Fonte: ").append(news.getNewsEditorial()).append("\n");
            result.append("--------------------------------------\n");
        }

        resultTextArea.setText(result.toString());
        newsContainer.add(resultTextArea);
        newsContainer.revalidate();
        newsContainer.repaint();
    }

    private void updateHistoryArea(String action) {
        String currentText = favoritesHistoryTextArea.getText();
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM"));
        favoritesHistoryTextArea.setText(currentText + "\n" + timestamp + " - " + action);
    }
}