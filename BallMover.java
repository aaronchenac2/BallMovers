package General;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class BallMover extends JPanel implements ActionListener, KeyListener
{
    JFrame jf = new JFrame();
    
    Timer tm = new Timer( 5, this );

    protected TextField tf;

    protected Label score1;

    protected int p1s = 0;

    protected Label score2;

    protected int p2s = 0;

    protected static final int HEIGHT = 525;

    protected static final int WIDTH = 1680;

    protected double y1 = 300;

    protected double y2 = 300;

    protected double velY1 = 1.5;

    protected double velY2 = 1.5;

    protected double ballx = WIDTH / 2;

    protected double bally = HEIGHT / 2;

    protected double velBall = 1.2;

    protected int ballAngle;

    protected Font hehe;

    protected static final int OFFSET = 0;

    public static final int PORT = 7777;
    
    private ServerSocket serverSocket = null;

    private Socket socket;

    private PrintStream output;

    private Scanner input;


    public BallMover() throws IOException
    {
        hehe = new Font( "Ariel", Font.PLAIN, 25 );
        Rectangle instructions = new Rectangle( WIDTH / 3 - 10, OFFSET );
        instructions.setLocation( 10, 0 );
        Rectangle score1Pos = new Rectangle( WIDTH / 3, OFFSET );
        score1Pos.setLocation( WIDTH / 3, 0 );
        Rectangle score2Pos = new Rectangle( WIDTH / 3, OFFSET );
        score2Pos.setLocation( 2 * WIDTH / 3, 0 );
        Rectangle screen = new Rectangle( WIDTH, HEIGHT );
        screen.setLocation( 0, 0 );

        tf = new TextField( "", 10 );
        tf.setBounds( new Rectangle( 5, 5 ) );
        tf.setFont( hehe );
        tf.setEditable( false );
        jf.add( tf );
        tf.addKeyListener( this );

        JLabel instruct = new JLabel( convertToMultiline(
            "Press: \n0 to restart \nq to change p1 padal direction \np to change p2 padal direction \ns to pause \ncy@ once you press 0"
                + "</html>" ) );
        instruct.setBounds( instructions );
        instruct.setFont( hehe );
        // jf.add( instruct );

        score1 = new Label( "Player 1 Score: " + p1s );
        score1.setBounds( score1Pos );
        score1.setFont( hehe );
        // jf.add( score1 );

        score2 = new Label( "Player 2 Score: " + p2s );
        score2.setBounds( score2Pos );
        score2.setFont( hehe );
        // jf.add( score2 );

        jf.setSize( WIDTH, HEIGHT );
        this.setBounds( screen );
        jf.add( this );
        jf.setTitle( "BallMover1" );
        jf.setVisible( true );

        if ( Math.random() > .5 )
        {
            if ( Math.random() > .5 )
            {
                ballAngle = (int)( Math.random() * 45 ) + 315;
            }
            else
            {
                ballAngle = (int)( Math.random() ) + 45;
            }
        }
        else
        {
            if ( Math.random() > .5 )
            {
                ballAngle = (int)( Math.random() * 45 ) + 135;
            }
            else
            {
                ballAngle = (int)( Math.random() * 45 ) + 180;
            }
        }
    }


    public static void main( String args[] ) throws IOException
    {
        BallMover b = new BallMover();
        b.startServer();
    }


    public void startServer() throws IOException
    {
        setServer();
        getConnection();
        setStreams();
        whilePlaying();
    }

    public void setServer() throws IOException
    {
        System.out.println( "Setting Server" );
        serverSocket = new ServerSocket( PORT );
        System.out.println( "Server Set" );
    }

    public void getConnection() throws IOException
    {
        System.out.println( "Waiting for client connection..." );
        socket = serverSocket.accept();
        System.out.println( "Client has connected!" );
    }

    public void setStreams() throws IOException
    {
        System.out.println( "Creating streams..." );
        output = new PrintStream( socket.getOutputStream() );
        output.flush();
        input = new Scanner( socket.getInputStream() );
        System.out.println( "Stream have been created!" );
    }
    
    public void whilePlaying()
    {
        tm.start();
        System.out.println( "Game started!" );
        while ( tm.isRunning() )
        {
            if (input.nextLine().equals( "turned" ))
            {
                velY2 *= -1;
            }
        }
    }

    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        g.setColor( Color.RED );
        g.fillRect( 30, (int)y1, 30, 200 );
        g.fillRect( WIDTH - 80, (int)y2, 30, 200 );
        g.fillOval( (int)ballx, (int)bally, 40, 40 );
        g.drawLine( 0, OFFSET, WIDTH, OFFSET );
    }


    @Override
    public void keyTyped( KeyEvent e )
    {
        char input = e.getKeyChar();
        if ( input == 's' )
        {
            if ( tm.isRunning() )
            {
                tm.stop();
            }
            else
            {
                tm.start();
            }
        }
        if ( input == 't' )
        {
            System.out.println( "ballx: " + ballx );
            System.out.println( "bally: " + bally );
            System.out.println( "delta x: " + velBall * Math.cos( Math.toRadians( ballAngle ) ) );
            System.out.println( "delta y: " + velBall * Math.sin( Math.toRadians( ballAngle ) ) );
            System.out.println( "ball angle: " + ballAngle );

        }
        if ( input == '0' )
        {
            velBall = 1.05;
            ballx = WIDTH / 2;
            bally = HEIGHT / 2;
            tm.start();
        }
        if ( input == 'q' )
        {
            velY1 *= -1;
        }
    }


    @Override
    public void keyPressed( KeyEvent e )
    {
    }


    @Override
    public void keyReleased( KeyEvent e )
    {
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            public void run()
            {
                if ( y1 + 210 > HEIGHT || y1 <= OFFSET ) // p1 padal
                {
                    if ( y1 <= OFFSET )
                    {
                        y1 = 5 + OFFSET;
                    }
                    velY1 *= -1;
                }
                if ( y2 + 210 > HEIGHT || y2 <= OFFSET )// p2 padal
                {
                    if ( y2 <= OFFSET )
                    {
                        y2 = 5 + OFFSET;
                    }
                    velY2 *= -1;
                }
                y1 = y1 + velY1;
                y2 = y2 + velY2;
                if ( ballx < 70 && ballx > 40 && bally >= y1 && bally <= y1 + 200 ) // bounce
                                                                                    // off
                                                                                    // p1
                {
                    System.out.println( "bdoink1" );
                    if ( Math.random() > .5 )
                    {
                        ballAngle = (int)( Math.random() * 45 ) + 315;
                    }
                    else
                    {
                        ballAngle = (int)( Math.random() ) + 45;
                    }
                    velBall += .03;
                }

                if ( ballx > WIDTH - 100 && ballx < WIDTH - 70 && bally >= y2 && bally <= y2 + 200 ) // bounce
                                                                                                     // off
                                                                                                     // p2
                {
                    System.out.println( "bdoink2" );
                    if ( Math.random() > .5 )
                    {
                        ballAngle = (int)( Math.random() * 45 ) + 135;
                    }
                    else
                    {
                        ballAngle = (int)( Math.random() * 45 ) + 180;
                    }
                    velBall += .03;
                }

                if ( ballx < 0 ) // Player 2 Wins
                {
                    p2s++;
                    score2.setText( "Player 2 Score: " + p2s );
                    tm.stop();
                }
                if ( ballx > WIDTH ) // Player 1 Wins
                {
                    p1s++;
                    score1.setText( "Player 1 Score: " + p1s );
                    tm.stop();
                }
                if ( bally > HEIGHT - 50 || bally <= OFFSET ) // touches bottom or top
                                                              // wall
                {
                    if ( bally <= OFFSET )
                    {
                        bally = 5 + OFFSET;
                    }
                    ballAngle *= -1;
                }
                ballx += velBall * Math.cos( Math.toRadians( ballAngle ) );
                bally += velBall * Math.sin( Math.toRadians( ballAngle ) );
                output.println( "" + y1 );
                output.flush();
                output.println( "" + y2 );
                output.flush();
                output.println( "" + ballx );
                output.flush();
                output.println( "" + bally );
                output.flush();
                repaint();
            }
        } );
    }


    public static String convertToMultiline( String orig )
    {
        return "<html>" + orig.replaceAll( "\n", "<br>" );
    }

}
