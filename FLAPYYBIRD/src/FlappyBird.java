import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener,  KeyListener{
    int boardWidth = 360;
    int boardHeigth = 640;

    //IMAGENS
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image backgroundover;



    //Bird
    int birdX = boardWidth/8;
    int birdy = boardHeigth/2;
    int birdWidth = 34;
    int birdHeigth = 24;

    class Bird {
        int x = birdX;
        int y = birdy;
        int width = birdWidth;
        int heigth = birdHeigth;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //PIPES 
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int heigth = pipeHeight;
        Image img;
        boolean passed = false;

        

        Pipe(Image img) {
            this.img = img;
        }

    }


    //Logica do jogo
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;  //Queda do passaro
    int BestScore = 0;      

    ArrayList<Pipe> pipes;
    Random random = new Random();


    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;



    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeigth));
        //setBackground(Color.BLUE);
        setFocusable(true);
        addKeyListener(this);

        //Carrgar imagens
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        backgroundover = new ImageIcon(getClass().getResource("./fundo1.png")).getImage();


        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();
        

        //game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();


    }

    public void placePipes() {
        //(0-1) * pipeHeigth/2 -> (0-256)
        //128
        // 0 - 128 - (0-256) --> pipeHeight/4 -> 3/4 pipeHeigth 

        int randomPipey = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeigth/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipey;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe); 

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //fundo
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeigth, null);


        //Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.heigth, null);

        //PIPES
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.heigth, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawImage(backgroundover, 0, 0, boardWidth, boardHeigth, null);
            g.drawString("Game Over: " + String.valueOf((int) score), 75, 280);
            g.drawString("Best Score: " + String.valueOf(BestScore), 75, 320);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

        if (score > BestScore) {
            BestScore = (int) score;
        }


    }


    public void move () {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //PIPES
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }



            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeigth) {
            gameOver = true;
        }       
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&  //Verificão se o passaro bateu ou não
               a.x + a.width > b.x &&
               a.y < b.y + b.heigth &&
               a.y + a.heigth > b.y;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // TECLA PARA O PASSARO PULAR.......................................................................
            velocityY = -9;
            if (gameOver) {
                // Reiniciar o Jogo e resetar o score
                bird.y = birdy;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
