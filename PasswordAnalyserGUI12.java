import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordAnalyserGUI12 extends JFrame {

    // ── Colors ─────────────────────────────────────────────────────────────
    private static final Color BG1         = new Color(15,  23,  42);
    private static final Color SIDEBAR_BG  = new Color(10,  15,  30);
    private static final Color ACCENT      = new Color(20,  184, 166);
    private static final Color ACCENT2     = new Color(99,  102, 241);
    private static final Color TEXT_PRI    = new Color(15,  23,  42);
    private static final Color TEXT_SEC    = new Color(100, 116, 139);
    private static final Color TEXT_LIGHT  = new Color(226, 232, 240);
    private static final Color TEXT_WHITE  = new Color(255, 255, 255);
    private static final Color BORDER_CLR  = new Color(203, 213, 225);
    private static final Color RED         = new Color(239, 68,  68);
    private static final Color AMBER       = new Color(245, 158, 11);
    private static final Color AMBER_DARK  = new Color(180, 110, 10);
    private static final Color GREEN_LT    = new Color(34,  197, 94);
    private static final Color GREEN_DK    = new Color(21,  128, 61);

    // ── Sidebar ────────────────────────────────────────────────────────────
    private boolean sidebarExpanded = true;
    private static final int SW_OPEN  = 210;
    private static final int SW_CLOSE = 58;
    private JPanel  sidebarPanel;
    private JLabel[] navTexts;

    // ── Widgets ────────────────────────────────────────────────────────────
    private JPasswordField  pwField;
    private JTextField      pwVisible;
    private JButton         toggleBtn;
    private boolean         isVisible = false;
    private JLabel          strengthLabel;
    private MeterBar        meterBar;
    private JLabel          lenCard, entCard, scoreCard, crackCard;
    private JLabel          commonBadge;
    private CheckRow[]      checkRows;
    private SuggestionPanel suggPanel;
    private RadarPanel      radarPanel;

    private static final String[] COMMON = {
        "123456","password","admin","qwerty","abc123","letmein","monkey",
        "dragon","master","iloveyou","sunshine","princess","welcome",
        "shadow","123456789","12345678","1234567","1234567890","000000","111111"
    };

    private static final String[][] NAV = {
        {"\uD83D\uDCC8", "Dashboard"},
        {"\uD83D\uDD11", "Analyser"},
        {"\uD83D\uDEE1", "Security Tips"},
        {"\uD83D\uDCCB", "History"},
        {"\u2699",       "Settings"},
        {"\u2753",       "Help"},
    };

    // ══════════════════════════════════════════════════════════════════════
    public PasswordAnalyserGUI12() {
        super("Password Analyser Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 820);
        setMinimumSize(new Dimension(780, 620));
        setLocationRelativeTo(null);
        buildLayout();
        setVisible(true);
    }

    private void buildLayout() {
        GradientBgPanel root = new GradientBgPanel();
        root.setLayout(new BorderLayout(0, 0));
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);
        mainArea.add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainArea.add(scroll, BorderLayout.CENTER);
        root.add(mainArea, BorderLayout.CENTER);
    }

    // ── Sidebar ────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        sidebarPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(20, 184, 166, 40));
                g2.fillRect(getWidth()-2, 0, 2, getHeight());
                g2.dispose();
            }
        };
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(SW_OPEN, 0));
        sidebarPanel.setOpaque(false);

        // Logo
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        logoRow.setOpaque(false);
        logoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        logoRow.setBorder(new EmptyBorder(18, 0, 18, 0));
        JLabel logo = new JLabel("\uD83D\uDD10  PassGuard");
        logo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        logo.setForeground(TEXT_WHITE);
        logoRow.add(logo);
        sidebarPanel.add(logoRow);
        sidebarPanel.add(makeDivider());

        // Nav items
        navTexts = new JLabel[NAV.length];
        for (int i = 0; i < NAV.length; i++) {
            final int idx = i;
            boolean active = (i == 1);

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (active) {
                        g2.setColor(new Color(20, 184, 166, 45));
                        g2.fillRoundRect(6, 2, getWidth()-12, getHeight()-4, 10, 10);
                        g2.setColor(ACCENT);
                        g2.fillRoundRect(0, 6, 3, getHeight()-12, 3, 3);
                    }
                    g2.dispose();
                }
            };
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            row.setBorder(new EmptyBorder(0, 14, 0, 10));
            row.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(NAV[i][0] + "  ");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            icon.setForeground(active ? ACCENT : new Color(148, 163, 184));

            JLabel txt = new JLabel(NAV[i][1]);
            txt.setFont(new Font(Font.SANS_SERIF, active ? Font.BOLD : Font.PLAIN, 13));
            txt.setForeground(active ? TEXT_WHITE : new Color(148, 163, 184));
            navTexts[i] = txt;

            row.add(icon);
            row.add(txt);

            row.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (!active) row.setBackground(new Color(255,255,255,8)); }
                public void mouseExited(MouseEvent e)  { row.setBackground(new Color(0,0,0,0)); }
            });

            sidebarPanel.add(row);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        sidebarPanel.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("  v2.0 Pro");
        ver.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        ver.setForeground(new Color(51, 65, 105));
        ver.setBorder(new EmptyBorder(0, 14, 16, 0));
        ver.setAlignmentX(LEFT_ALIGNMENT);
        sidebarPanel.add(ver);

        return sidebarPanel;
    }

    private Component makeDivider() {
        JPanel d = new JPanel() {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(255,255,255,18));
                g.fillRect(12, 3, getWidth()-24, 1);
            }
        };
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        d.setPreferredSize(new Dimension(0, 8));
        return d;
    }

    // ── Header ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(15, 23, 42, 200));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,12));
                g2.fillRect(0, getHeight()-1, getWidth(), 1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(new EmptyBorder(0, 14, 0, 20));

        // Hamburger
        JButton ham = new JButton("\u2630");
        ham.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        ham.setForeground(TEXT_LIGHT);
        ham.setContentAreaFilled(false);
        ham.setBorderPainted(false);
        ham.setFocusPainted(false);
        ham.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ham.setBorder(new EmptyBorder(4, 4, 4, 16));
        ham.addActionListener(e -> toggleSidebar());
        header.add(ham, BorderLayout.WEST);

        JLabel title = new JLabel("Password Analyser");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        title.setForeground(TEXT_WHITE);
        header.add(title, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel badge = new RoundedLabel("  SECURE TOOL  ", ACCENT, new Color(5,45,35), 10);
        badge.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        right.add(badge);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Sidebar toggle animation ────────────────────────────────────────────
    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        for (JLabel l : navTexts) l.setVisible(sidebarExpanded);
        int target = sidebarExpanded ? SW_OPEN : SW_CLOSE;
        int[] cur = {sidebarPanel.getPreferredSize().width};
        Timer t = new Timer(10, null);
        t.addActionListener(e -> {
            int step = (target - cur[0]);
            step = step > 0 ? Math.max(1, step/3) : Math.min(-1, step/3);
            cur[0] += step;
            if (Math.abs(cur[0] - target) <= 1) { cur[0] = target; ((Timer)e.getSource()).stop(); }
            sidebarPanel.setPreferredSize(new Dimension(cur[0], 0));
            revalidate();
        });
        t.start();
    }

    // ── Content ────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(24, 24, 32, 24));

        // Page heading
        addTo(wrap, boldLabel("Password Strength Analyser", 22, TEXT_WHITE));
        wrap.add(Box.createRigidArea(new Dimension(0, 4)));
        addTo(wrap, boldLabel("Real-time security analysis — entropy, crack time & visual metrics", 13, new Color(148,163,184)));
        wrap.add(Box.createRigidArea(new Dimension(0, 22)));

        // ── Input card ─────────────────────────────────────────────────────
        JPanel inputCard = glassCard();
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));
        addTo(inputCard, boldLabel("Enter your password", 14, TEXT_PRI));
        inputCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        inputRow.setAlignmentX(LEFT_ALIGNMENT);

        pwField = new JPasswordField();
        pwField.setFont(new Font("Monospaced", Font.PLAIN, 15));
        pwField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(99,102,241,130), 1, true),
            new EmptyBorder(8, 14, 8, 14)));
        pwField.setBackground(new Color(248, 250, 252));
        pwField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { analyse(new String(pwField.getPassword())); }
        });

        pwVisible = new JTextField();
        pwVisible.setFont(new Font("Monospaced", Font.PLAIN, 15));
        pwVisible.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(99,102,241,130), 1, true),
            new EmptyBorder(8, 14, 8, 14)));
        pwVisible.setBackground(new Color(248, 250, 252));
        pwVisible.setVisible(false);
        pwVisible.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { analyse(pwVisible.getText()); }
        });

        JPanel fields = new JPanel(new CardLayout());
        fields.setOpaque(false);
        fields.add(pwField, "hidden");
        fields.add(pwVisible, "visible");

        toggleBtn = new AccentButton("Show", ACCENT2);
        toggleBtn.setPreferredSize(new Dimension(72, 38));
        toggleBtn.addActionListener(e -> toggleVisibility());

        inputRow.add(fields, BorderLayout.CENTER);
        inputRow.add(toggleBtn, BorderLayout.EAST);
        inputCard.add(inputRow);
        inputCard.add(Box.createRigidArea(new Dimension(0, 14)));

        meterBar = new MeterBar();
        meterBar.setAlignmentX(LEFT_ALIGNMENT);
        meterBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        inputCard.add(meterBar);
        inputCard.add(Box.createRigidArea(new Dimension(0, 7)));

        strengthLabel = new JLabel("Enter a password to analyse");
        strengthLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        strengthLabel.setForeground(TEXT_SEC);
        strengthLabel.setAlignmentX(LEFT_ALIGNMENT);
        inputCard.add(strengthLabel);
        wrap.add(inputCard);
        wrap.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Metric cards ───────────────────────────────────────────────────
        JPanel metricsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        metricsRow.setOpaque(false);
        metricsRow.setAlignmentX(LEFT_ALIGNMENT);
        metricsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        lenCard   = metricCard(metricsRow, "Length",         "\uD83D\uDCCF", new Color(99,102,241));
        entCard   = metricCard(metricsRow, "Entropy (bits)", "\uD83C\uDFB2", ACCENT);
        scoreCard = metricCard(metricsRow, "Score /10",      "\u2B50",       AMBER);
        crackCard = metricCard(metricsRow, "Crack time",     "\u23F1",       RED);
        wrap.add(metricsRow);
        wrap.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Common badge ───────────────────────────────────────────────────
        commonBadge = new JLabel("  \u26A0  This password is very common \u2014 easily guessed!  ");
        commonBadge.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        commonBadge.setOpaque(true);
        commonBadge.setBackground(new Color(254, 226, 226));
        commonBadge.setForeground(new Color(153, 27, 27));
        commonBadge.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 4, 0, 0, RED),
            new EmptyBorder(9, 12, 9, 12)));
        commonBadge.setVisible(false);
        commonBadge.setAlignmentX(LEFT_ALIGNMENT);
        wrap.add(commonBadge);

        // ── Two-column: checks | radar ──────────────────────────────────────
        JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
        twoCol.setOpaque(false);
        twoCol.setAlignmentX(LEFT_ALIGNMENT);
        twoCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 295));

        JPanel checksCard = glassCard();
        checksCard.setLayout(new BoxLayout(checksCard, BoxLayout.Y_AXIS));
        addTo(checksCard, boldLabel("Character Checks", 13, new Color(51,65,85)));
        checksCard.add(Box.createRigidArea(new Dimension(0, 9)));
        String[] chkLabels = {
            "At least 8 characters","At least 12 characters",
            "Lowercase letters (a-z)","Uppercase letters (A-Z)",
            "Numbers (0-9)","Special characters (!@#...)","Not a common password"
        };
        checkRows = new CheckRow[chkLabels.length];
        for (int i = 0; i < chkLabels.length; i++) {
            checkRows[i] = new CheckRow(chkLabels[i]);
            checkRows[i].setAlignmentX(LEFT_ALIGNMENT);
            checksCard.add(checkRows[i]);
            if (i < chkLabels.length-1) checksCard.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        twoCol.add(checksCard);

        JPanel radarCard = glassCard();
        radarCard.setLayout(new BoxLayout(radarCard, BoxLayout.Y_AXIS));
        addTo(radarCard, boldLabel("Strength by Category", 13, new Color(51,65,85)));
        radarCard.add(Box.createRigidArea(new Dimension(0, 6)));
        radarPanel = new RadarPanel();
        radarPanel.setOpaque(false);
        radarPanel.setAlignmentX(LEFT_ALIGNMENT);
        radarCard.add(radarPanel);
        twoCol.add(radarCard);

        wrap.add(Box.createRigidArea(new Dimension(0, 4)));
        wrap.add(twoCol);
        wrap.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Suggestions ────────────────────────────────────────────────────
        JPanel sugCard = glassCard();
        sugCard.setLayout(new BoxLayout(sugCard, BoxLayout.Y_AXIS));
        addTo(sugCard, boldLabel("Suggestions", 13, new Color(51,65,85)));
        sugCard.add(Box.createRigidArea(new Dimension(0, 10)));
        suggPanel = new SuggestionPanel();
        suggPanel.setOpaque(false);
        suggPanel.setAlignmentX(LEFT_ALIGNMENT);
        sugCard.add(suggPanel);
        wrap.add(sugCard);

        return wrap;
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private JPanel glassCard() {
        JPanel card = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 225));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 70));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return card;
    }

    private JLabel boldLabel(String text, int size, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
        l.setForeground(c);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void addTo(JPanel p, JLabel l) { l.setAlignmentX(LEFT_ALIGNMENT); p.add(l); }

    private JLabel metricCard(JPanel parent, String subtitle, String icon, Color accent) {
        JPanel card = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 218));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 210));
                g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 10, 12, 10));

        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        ic.setAlignmentX(CENTER_ALIGNMENT);

        JLabel val = new JLabel("—");
        val.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        val.setForeground(TEXT_PRI);
        val.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel(subtitle);
        sub.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        sub.setForeground(TEXT_SEC);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(ic);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(val);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(sub);
        card.add(Box.createVerticalGlue());
        parent.add(card);
        return val;
    }

    // ── Toggle visibility ──────────────────────────────────────────────────
    private void toggleVisibility() {
        isVisible = !isVisible;
        JPanel fp = (JPanel) pwField.getParent();
        if (isVisible) {
            pwVisible.setText(new String(pwField.getPassword()));
            ((CardLayout) fp.getLayout()).show(fp, "visible");
            toggleBtn.setText("Hide");
        } else {
            pwField.setText(pwVisible.getText());
            ((CardLayout) fp.getLayout()).show(fp, "hidden");
            toggleBtn.setText("Show");
        }
    }

    // ── Analysis ───────────────────────────────────────────────────────────
    private void analyse(String pw) {
        if (pw == null || pw.isEmpty()) {
            meterBar.setPercent(0, new Color(100,116,139));
            strengthLabel.setText("Enter a password to analyse");
            strengthLabel.setForeground(TEXT_SEC);
            lenCard.setText("—"); entCard.setText("—"); scoreCard.setText("—"); crackCard.setText("—");
            commonBadge.setVisible(false);
            for (CheckRow r : checkRows) r.setResult(false);
            radarPanel.setScores(new double[]{0,0,0,0,0,0});
            suggPanel.setSuggestions(new String[0], new String[0]);
            return;
        }
        boolean lo = pw.matches(".*[a-z].*"), up = pw.matches(".*[A-Z].*");
        boolean num = pw.matches(".*[0-9].*"), sp = pw.matches(".*[^a-zA-Z0-9].*");
        boolean com = isCommon(pw);
        int len = pw.length(), ent = entropy(pw), sc = score(len, lo, up, num, sp, com);

        Color barClr = barColor(sc);
        meterBar.setPercent(sc * 10, barClr);
        strengthLabel.setText("Strength: " + strengthText(sc));
        strengthLabel.setForeground(barClr);
        lenCard.setText(String.valueOf(len));
        entCard.setText(String.valueOf(ent));
        scoreCard.setText(sc + "/10");
        crackCard.setText(crackTime(ent));
        commonBadge.setVisible(com);

        boolean[] res = {len>=8, len>=12, lo, up, num, sp, !com};
        for (int i = 0; i < checkRows.length; i++) checkRows[i].setResult(res[i]);

        radarPanel.setScores(new double[]{
            Math.min(len/16.0,1.0)*10, lo?10:0, up?10:0, num?10:0, sp?10:0, com?0:10
        });

        List<String> msgs = new ArrayList<>(), types = new ArrayList<>();
        if (com)   { msgs.add("Avoid common passwords like \"password\" or \"123456\"."); types.add("bad"); }
        if (len<8) { msgs.add("Use at least 8 characters."); types.add("bad"); }
        else if(len<12){ msgs.add("Consider 12+ characters for stronger security."); types.add("warn"); }
        if (!up)   { msgs.add("Add uppercase letters (A-Z)."); types.add("warn"); }
        if (!lo)   { msgs.add("Add lowercase letters (a-z)."); types.add("warn"); }
        if (!num)  { msgs.add("Include numbers (0-9)."); types.add("warn"); }
        if (!sp)   { msgs.add("Add special characters like @, #, $, ! etc."); types.add("warn"); }
        if (msgs.isEmpty()) { msgs.add("Excellent! Your password is very strong. \uD83D\uDCAA"); types.add("ok"); }
        suggPanel.setSuggestions(msgs.toArray(new String[0]), types.toArray(new String[0]));
    }

    private boolean isCommon(String pw) {
        for (String c : COMMON) if (c.equalsIgnoreCase(pw)) return true;
        return false;
    }
    private int entropy(String pw) {
        int s=0;
        if(pw.matches(".*[a-z].*"))s+=26; if(pw.matches(".*[A-Z].*"))s+=26;
        if(pw.matches(".*[0-9].*"))s+=10; if(pw.matches(".*[^a-zA-Z0-9].*"))s+=32;
        if(s==0)s=1;
        return (int)(pw.length()*(Math.log(s)/Math.log(2)));
    }
    private int score(int len,boolean lo,boolean up,boolean num,boolean sp,boolean com){
        int s=0;
        if(len>=6)s++; if(len>=8)s++; if(len>=12)s++; if(len>=16)s++;
        if(lo)s++; if(up)s++; if(num)s++; if(sp)s+=2; if(!com)s++;
        return Math.min(s,10);
    }
    private String strengthText(int sc){
        if(sc<=2)return "Very Weak"; if(sc<=4)return "Weak";
        if(sc<=6)return "Fair";      if(sc<=8)return "Strong";
        return "Very Strong";
    }
    private Color barColor(int sc){
        if(sc<=2)return RED; if(sc<=4)return AMBER;
        if(sc<=6)return AMBER_DARK; if(sc<=8)return GREEN_LT;
        return GREEN_DK;
    }
    private String crackTime(int ent){
        double g=Math.pow(2,ent),r=1e10,s=g/r;
        if(s<1)return "<1s"; if(s<60)return (int)s+"s";
        if(s<3600)return (int)(s/60)+"m"; if(s<86400)return (int)(s/3600)+"h";
        if(s<2592000)return (int)(s/86400)+"d";
        if(s<31536000)return (int)(s/2592000)+"mo";
        double y=s/31536000;
        if(y<1000)return (int)y+"y"; if(y<1e6)return (int)(y/1000)+"Ky";
        return ">1My";
    }

    // ══════════════════════════════════════════════════════════════════════
    // Inner components
    // ══════════════════════════════════════════════════════════════════════

    static class GradientBgPanel extends JPanel {
        GradientBgPanel() { setOpaque(true); }
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0,0,new Color(15,23,42),getWidth(),getHeight(),new Color(28,25,70)));
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Blobs
            g2.setColor(new Color(99,102,241,22));
            g2.fillOval(-100,-100,380,380);
            g2.setColor(new Color(20,184,166,18));
            g2.fillOval(getWidth()-220,getHeight()-220,440,440);
            g2.setColor(new Color(245,158,11,11));
            g2.fillOval(getWidth()/2-120,getHeight()/2-120,320,320);
            // Dot grid
            g2.setColor(new Color(255,255,255,7));
            for(int x=20;x<getWidth();x+=40)
                for(int y=20;y<getHeight();y+=40)
                    g2.fillOval(x-1,y-1,2,2);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class MeterBar extends JComponent {
        private int percent=0; private Color color=new Color(100,116,139);
        MeterBar(){setPreferredSize(new Dimension(600,10));}
        void setPercent(int p,Color c){percent=p;color=c;repaint();}
        public void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight();
            g2.setColor(new Color(226,232,240));
            g2.fillRoundRect(0,0,w,h,h,h);
            int f=(int)(w*percent/100.0);
            if(f>0){
                g2.setPaint(new GradientPaint(0,0,color.brighter(),f,0,color));
                g2.fillRoundRect(0,0,f,h,h,h);
            }
            g2.dispose();
        }
    }

    static class CheckRow extends JPanel {
        private final DotIcon dot;
        CheckRow(String text){
            setLayout(new FlowLayout(FlowLayout.LEFT,6,0));
            setOpaque(false);
            dot=new DotIcon(false);
            JLabel lbl=new JLabel(text);
            lbl.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,13));
            lbl.setForeground(new Color(30,41,59));
            add(dot); add(lbl);
            setMaximumSize(new Dimension(Integer.MAX_VALUE,26));
        }
        void setResult(boolean ok){dot.setOk(ok);repaint();}
    }

    static class DotIcon extends JComponent {
        private boolean ok;
        DotIcon(boolean ok){this.ok=ok;setPreferredSize(new Dimension(18,18));}
        void setOk(boolean ok){this.ok=ok;repaint();}
        public void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ok?new Color(220,252,231):new Color(254,226,226));
            g2.fillOval(0,0,17,17);
            g2.setColor(ok?new Color(21,128,61):new Color(185,28,28));
            g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,10));
            FontMetrics fm=g2.getFontMetrics();
            String ch=ok?"\u2713":"\u2717";
            g2.drawString(ch,(17-fm.stringWidth(ch))/2,(17+fm.getAscent()-fm.getDescent())/2);
            g2.dispose();
        }
    }

    static class SuggestionPanel extends JPanel {
        SuggestionPanel(){setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));setOpaque(false);}
        void setSuggestions(String[] msgs,String[] types){
            removeAll();
            for(int i=0;i<msgs.length;i++){
                SugRow row=new SugRow(msgs[i],types[i]);
                row.setAlignmentX(LEFT_ALIGNMENT);
                add(row);
                if(i<msgs.length-1)add(Box.createRigidArea(new Dimension(0,7)));
            }
            revalidate();repaint();
        }
    }

    static class SugRow extends JPanel {
        private final Color bg;
        SugRow(String text,String type){
            setLayout(new BorderLayout());
            Color fg,accent;
            switch(type){
                case"bad":  bg=new Color(254,226,226);fg=new Color(127,29,29);  accent=new Color(239,68,68);  break;
                case"warn": bg=new Color(254,243,199);fg=new Color(92,45,5);    accent=new Color(245,158,11); break;
                default:    bg=new Color(220,252,231);fg=new Color(20,83,45);   accent=new Color(34,197,94);  break;
            }
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0,4,0,0,accent),
                new EmptyBorder(9,12,9,12)));
            JLabel l=new JLabel(text);
            l.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,13));
            l.setForeground(fg);
            add(l,BorderLayout.CENTER);
            setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        }
        public void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
            g2.dispose();
        }
    }

    static class RadarPanel extends JPanel {
        private double[] scores=new double[]{0,0,0,0,0,0};
        private final String[] labels={"Length","Lowercase","Uppercase","Numbers","Special","Uniqueness"};
        RadarPanel(){setPreferredSize(new Dimension(0,220));}
        void setScores(double[] s){scores=s;repaint();}
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight(),cx=w/2,cy=h/2,r=Math.min(w,h)/2-52,n=scores.length;
            for(int ring=1;ring<=5;ring++){
                double frac=ring/5.0;
                int[]gx=new int[n],gy=new int[n];
                for(int i=0;i<n;i++){double a=Math.toRadians(-90+i*360.0/n);gx[i]=(int)(cx+frac*r*Math.cos(a));gy[i]=(int)(cy+frac*r*Math.sin(a));}
                g2.setColor(new Color(30,41,59,55));
                g2.setStroke(new BasicStroke(0.7f));
                g2.drawPolygon(gx,gy,n);
            }
            g2.setColor(new Color(30,41,59,70));
            for(int i=0;i<n;i++){double a=Math.toRadians(-90+i*360.0/n);g2.drawLine(cx,cy,(int)(cx+r*Math.cos(a)),(int)(cy+r*Math.sin(a)));}
            int[]dx=new int[n],dy=new int[n];
            for(int i=0;i<n;i++){double a=Math.toRadians(-90+i*360.0/n),frac=scores[i]/10.0;dx[i]=(int)(cx+frac*r*Math.cos(a));dy[i]=(int)(cy+frac*r*Math.sin(a));}
            g2.setColor(new Color(20,184,166,50));
            g2.fillPolygon(dx,dy,n);
            g2.setColor(new Color(20,184,166));
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawPolygon(dx,dy,n);
            for(int i=0;i<n;i++){
                g2.setColor(Color.WHITE);g2.fillOval(dx[i]-5,dy[i]-5,10,10);
                g2.setColor(new Color(20,184,166));g2.setStroke(new BasicStroke(2f));g2.drawOval(dx[i]-5,dy[i]-5,10,10);
            }
            g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
            g2.setColor(new Color(51,65,85));
            FontMetrics fm=g2.getFontMetrics();
            for(int i=0;i<n;i++){
                double a=Math.toRadians(-90+i*360.0/n);
                int lx=(int)(cx+(r+32)*Math.cos(a)),ly=(int)(cy+(r+32)*Math.sin(a));
                g2.drawString(labels[i],lx-fm.stringWidth(labels[i])/2,ly+fm.getAscent()/2-2);
            }
            g2.dispose();
        }
    }

    static class AccentButton extends JButton {
        private final Color accent;
        AccentButton(String text,Color accent){
            super(text);this.accent=accent;
            setFocusPainted(false);setContentAreaFilled(false);setBorderPainted(false);
            setFont(new Font(Font.SANS_SERIF,Font.BOLD,12));
            setForeground(Color.WHITE);setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        public void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed()?accent.darker():accent);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedLabel extends JLabel {
        private final Color bg;
        RoundedLabel(String text,Color bg,Color fg,int pad){
            super(text);this.bg=bg;
            setOpaque(false);setForeground(fg);
            setBorder(new EmptyBorder(4,pad,4,pad));
        }
        public void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
            g2.dispose();super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(PasswordAnalyserGUI12::new);
    }
}