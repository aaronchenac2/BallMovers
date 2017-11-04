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
import javax.swing.Timer;


public class BallMover2 extends JPanel implements ActionListener, KeyListener
{
    JFrame jf = new JFrame();

    Timer tm = new Timer( 5, this );

    protected TextField tf;

    private static final int HEIGHT = 525;

    private static final int WIDTH = 1680;

    private double y1 = 300;

    private double y2 = 300;

    private double ballx = WIDTH / 2;

    private double bally = HEIGHT / 2;

    protected static final int OFFSET = 0;

    public static final int PORT = 7777;

    public static final String IP = "localhost";

    private Socket socket;

    private PrintStream output;

    private Scanner input;

    private int counter = 1;


    public BallMover2()
    {
        tf = new TextField( "", 10 );
        tf.setBounds( new Rectangle( 5, 5 ) );
        tf.setEditable( false );
        jf.add( tf );
        tf.addKeyListener( this );

        jf.setSize( WIDTH, HEIGHT );
        jf.add( this );
        jf.setTitle( "BallMover2" );
        jf.setVisible( true );

    }


    public static void main( String args[] )
    {
        BallMover2 b2 = new BallMover2();
        b2.startClient();
    }


    public void startClient()
    {
        try
        {
            setClient();
            setStreams();
            whilePlaying();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    public void setClient() throws IOException
    {
        System.out.println( "Connecting to Server..." );
        socket = new Socket( IP, PORT );
        System.out.println( "Connected to Server!" );
    }


    private void setStreams() throws IOException
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
            y1 = Double.parseDouble( input.nextLine() );
            y2 = Double.parseDouble( input.nextLine() );
            ballx = Double.parseDouble( input.nextLine() );
            bally = Double.parseDouble( input.nextLine() );
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
    public void keyPressed( KeyEvent e )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyReleased( KeyEvent e )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyTyped( KeyEvent e )
    {
        char input = e.getKeyChar();
        if ( input == 'p' )
        {
            output.println( "turned" );
            output.flush();
        }
    }


    @Override
    public void actionPerformed( ActionEvent arg0 )
    {
        repaint();
    }
}