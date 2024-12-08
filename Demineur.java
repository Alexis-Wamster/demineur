import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Demineur2 {
    public static void main(String[] args) {
        boolean isPlaying = true;
        int action;
        Point squareClicked = new Point();
        Grid gameMap = new Grid(10,8, 10);
        WindowsGraph windowsGame = new WindowsGraph(gameMap);

    }
}

class Grid{
    public int nbColumn;
    public int nbRow;
    public int nbBombs;
    public int nbDugSquare;
    public int nbFlagToPlace;
    public Square[][] grid;
    public JPanel GraphicGrid;
    public final static char[] falg = {'¤','$','£','€'};

    public Grid(int nbColumn, int nbRow){
        this.nbColumn = nbColumn;
        this.nbRow = nbRow;
        this.nbBombs = 0;
        this.nbDugSquare = 0;
        this.nbFlagToPlace = 0;
        this.createBlankGrid();
    }

    public Grid(int nbColumn, int nbRow, int nbBombs){
        this.nbColumn = nbColumn;
        this.nbRow = nbRow;
        this.nbBombs = nbBombs;
        this.nbDugSquare = 0;
        this.nbFlagToPlace = nbBombs;
        this.createBlankGrid();
    }

    public Grid(int nbColumn, int nbRow, int nbBombs, Point click){
        this.nbColumn = nbColumn;
        this.nbRow = nbRow;
        this.nbBombs = nbBombs;
        this.nbDugSquare = 0;
        this.nbFlagToPlace = nbBombs;
        this.createBlankGrid();
        this.addBombs(click);
        this.addNumber();
    }

    public void createBlankGrid() {
        this.grid = new Square[this.nbColumn][this.nbRow];
        for (int y=0 ; y<this.nbRow ; y++){
            for (int x=0 ; x < this.nbColumn ; x++){
                if ((y%2==0 && x%2==0) || (y%2!=0 && x%2!=0)) {
                    this.grid[x][y] = new Square(0, false, false);
                }
                else{
                    this.grid[x][y] = new Square(0, false, true);
                }
            }
        }
    }

    public void addBombs(Point click){
        int choosenLine;
        int choosenColumn;
        Point bombPosition;
        Point[] squareProtected = click.getPointAround(this, true);
        if (this.nbBombs >= this.nbColumn*this.nbRow-9){
            System.out.println("Error: la grille ne peut pas accueillir toutes les bombes");
        }
        for (int bomb=1 ; bomb <= this.nbBombs ; bomb++){
            do{
                choosenLine = (int) (Math.random() * this.nbRow);
                choosenColumn = (int) (Math.random() * this.nbColumn);
                bombPosition = new Point(choosenColumn, choosenLine);
            } while (9 == this.grid[choosenColumn][choosenLine].value || bombPosition.isInTab(squareProtected));
            this.grid[choosenColumn][choosenLine].value = 9;
        }
        this.addNumber();
        this.dig2(click);
    }

    private void addNumber(){
        Point[] aroundPointList;
        int nbBombAround;
        for (int y=0 ; y<this.nbRow ; y++) {
            for (int x = 0; x < this.nbColumn; x++) {
                if (this.grid[x][y].value != 9){
                    aroundPointList = (new Point(x,y)).getPointAround(this, false);
                    nbBombAround = 0;
                    for (Point aroundPoint : aroundPointList){
                        if (aroundPoint!=null) {
                            if (this.grid[aroundPoint.x][aroundPoint.y].value == 9) {
                                nbBombAround++;
                            }
                        }
                    }
                    this.grid[x][y].value = nbBombAround;
                }
            }
        }
    }

    public boolean action(int action, Point squareClicked){
        if (action==0){
            if (this.grid[squareClicked.x][squareClicked.y].flag==0){
                if (this.nbDugSquare==0){
                    this.addBombs(squareClicked);
                }
                else if (this.dig2(squareClicked)==false){
                    return false;
                }
            }
        }
        else if (action>0 && action<5) {
            if (this.grid[squareClicked.x][squareClicked.y].flag == action){
                this.grid[squareClicked.x][squareClicked.y].flag = 0;
                this.nbFlagToPlace ++;
            }
            else{
                this.grid[squareClicked.x][squareClicked.y].flag = action;
                this.nbFlagToPlace --;
            }
        }
        return true;
    }

    public boolean dig2(Point square){
        if (this.grid[square.x][square.y].isShown == true){
            return true;
        }
        this.grid[square.x][square.y].isShown = true;
        this.nbDugSquare ++;
        if (this.grid[square.x][square.y].value == 9){
            return false;
        }
        if (this.grid[square.x][square.y].value != 0){
            return true;
        }
        Point[] aroundPointList = square.getPointAround(this, false);
        for (Point aroundPoint : aroundPointList){
            if (aroundPoint!=null) {
                this.dig2(aroundPoint);
            }
        }
        return true;
    }
}

class Point extends JFrame{
    public int x;
    public int y;

    public Point(){
        this.x = 0;
        this.y = 0;
    }
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean isInTab(Point[] tab){
        for (Point point: tab){
            if (point != null) {
                if (this.x == point.x && this.y == point.y) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPointExist(Grid grid){
        if (this.x < 0 || this.y < 0 || this.x >= grid.nbColumn || this.y >= grid.nbRow){
            return false;
        }
        return true;
    }

    public Point[] getPointAround(Grid grid, boolean isCenterIncluded){
        Point[] aroundPoint = new Point[9];
        int sizeOfAroundPoint = 0;
        for (int y=-1; y<=1; y++){
            int newPointY = y + this.y;
            if (newPointY>=0 && newPointY< grid.nbRow) {
                for (int x=-1; x<=1; x++){
                    int newPointX = x + this.x;
                    if (newPointX>=0 && newPointX< grid.nbColumn) {
                        if (isCenterIncluded == true || this.x != newPointX || this.y != newPointY) {
                            aroundPoint[sizeOfAroundPoint] = (new Point(newPointX, newPointY));
                            sizeOfAroundPoint++;
                        }
                    }
                }
            }
        }
        return aroundPoint;
    }
}

class Square{
    public int value;
    public int flag;
    public boolean isShown;
    public boolean isDark;
    public Square(int value, boolean isShown, boolean isDark){
        this.value = value;
        this.isShown = isShown;
        this.isDark = isDark;
        this.flag = 0;
    }
}

class WindowsGraph{
    public static final ImageIcon[] number = {new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_1.png"),
            new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_2.png"),
            new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_3.png"),
            new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_4.png"),
            new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_5.png"),
            new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_6.png"),
            new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_7.png"),
            new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/chiffre_8.png")};

    public static final ImageIcon flag = new ImageIcon("C:/Users/nerol/OneDrive - UPEC/IUT/DEV/SAé_Demineur/drapeau_rouge.png");
    private static final Color vert1 = new Color(9, 82, 40, 255);
    private static final Color vert2 = new Color(58, 157, 35);
    private static final Color vert3 = new Color(159, 232, 85);
    private static final Color vert4 = new Color(176, 242, 182);

    private static final Color marron2 = new Color(180, 161, 131);
    private static final Color marron3 = new Color(223, 208, 192);
    private JFrame fenetre;
    private JPanel mainContainer;
    private JLabel header;
    private JPanel gridGraphContainer;
    private GridLayout gridGraph;
    public WindowsGraph(Grid grid){

        this.gridGraphContainer = new JPanel();

        this.header = new JLabel("En-tête");
        this.header.setBackground(this.vert1);
        this.header.setForeground(Color.WHITE);
        this.header.setOpaque(true);
        //this.header.setPreferredSize(new Dimension(this.fenetre.getWidth(), 80));

        this.mainContainer = new JPanel(new BorderLayout());
        this.mainContainer.add(this.gridGraphContainer, BorderLayout.CENTER);
        this.mainContainer.add(this.header, BorderLayout.NORTH);

        this.fenetre = new JFrame("Demineur");
        this.fenetre.setSize(500, 480);
        this.fenetre.setLocation(0, 0);
        this.fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.fenetre.add(this.mainContainer);

        changeGrid(grid);
    }

    public void changeGrid(Grid grid) {
        this.gridGraphContainer.removeAll();
        this.gridGraph = new GridLayout(grid.nbRow, grid.nbColumn);
        this.gridGraphContainer.setLayout(this.gridGraph);
        ImagePanel[][] square = new ImagePanel[grid.nbColumn][grid.nbRow];
        for (int y = 0; y < grid.nbRow; y++) {
            for (int x = 0; x < grid.nbColumn; x++) {
                final int xFinal = x;
                final int yFinal = y;
                square[x][y] = new ImagePanel(grid.grid[x][y].value-1, grid.grid[x][y].isShown, grid.grid[x][y].flag);
                square[x][y].setLayout(new BorderLayout());
                if (grid.grid[x][y].isShown){
                    if (grid.grid[x][y].isDark) {
                        square[x][y].setBackground(WindowsGraph.marron2);
                    } else {
                        square[x][y].setBackground(WindowsGraph.marron3);
                    }
                }
                else {
                    if (grid.grid[x][y].isDark) {
                        square[x][y].setBackground(WindowsGraph.vert2);
                    } else {
                        square[x][y].setBackground(WindowsGraph.vert3);
                    }
                    square[x][y].addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) {
                            square[xFinal][yFinal].removeAll();
                            square[xFinal][yFinal].setBackground(WindowsGraph.vert4);
                            square[xFinal][yFinal].revalidate();
                            square[xFinal][yFinal].repaint();
                        }

                        public void mouseExited(MouseEvent e) {
                            square[xFinal][yFinal].removeAll();

                            if (grid.grid[xFinal][yFinal].isDark) {
                                square[xFinal][yFinal].setBackground(WindowsGraph.vert2);
                            } else {
                                square[xFinal][yFinal].setBackground(WindowsGraph.vert3);
                            }
                            square[xFinal][yFinal].revalidate();
                            square[xFinal][yFinal].repaint();

                        }

                        public void mousePressed(MouseEvent e) {
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                boolean isPlaying = grid.action(0, new Point(xFinal, yFinal));
                                changeGrid(grid);
                                if (isPlaying==false){
                                    System.out.println("Perdu !!");
                                    System.exit(0);
                                }
                                if (grid.nbDugSquare == grid.nbRow*grid.nbColumn - grid.nbBombs){
                                    System.out.println("gagné !!");
                                    System.exit(0);
                                }
                            }
                            else if (e.getButton() == MouseEvent.BUTTON3) {
                                grid.action(1, new Point(xFinal, yFinal));
                                changeGrid(grid);
                            }
                        }
                    });
                }
                this.gridGraphContainer.add(square[x][y]);
            }
        }
        this.fenetre.setVisible(true);
    }
}

class ImagePanel extends JPanel {
    public int value;
    public boolean isShown;
    public int flag;

    public ImagePanel(int value, boolean isShown, int flag){
        this.value = value;
        this.isShown = isShown;
        this.flag = flag;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        if (this.value >= 0 && this.value < 8 && this.isShown==true){
            g.drawImage(WindowsGraph.number[this.value].getImage(), 0, 0, width, height, this);
        }
        if (this.isShown==false && flag>0){
            g.drawImage(WindowsGraph.flag.getImage(), 0, 0, width, height, this);
        }
    }
}