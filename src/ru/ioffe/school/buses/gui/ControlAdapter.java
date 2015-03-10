package ru.ioffe.school.buses.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ControlAdapter implements KeyListener, ActionListener, 
		MouseListener, MouseMotionListener, ListSelectionListener, ChangeListener {

	private GUIControl c;
	
	public ControlAdapter(GUIControl control) {
		this.c = control;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == c.updateScreenTimer) {
			c.updateScreen();
		} else if (e.getSource() == c.updateTimeTimer) {
			c.updateTime();
		} else if (e.getSource() == c.zoomButton) {
			c.model.zoom(c.percent);
			c.view.updateMap();
		} else if (e.getSource() == c.unzoomButton) {
			c.model.zoom(-c.percent);
			c.view.updateMap();
		} else if (e.getSource() == c.upButton) {
			c.model.moveVert(c.percent);
			c.view.updateMap();
		} else if (e.getSource() == c.downButton) {
			c.model.moveVert(-c.percent);
			c.view.updateMap();
		} else if (e.getSource() == c.rightButton) {
			c.model.moveHoriz(c.percent);
			c.view.updateMap();
		} else if (e.getSource() == c.leftButton) {
			c.model.moveHoriz(-c.percent);
			c.view.updateMap();
		} else if (e.getSource() == c.pauseButton) {
			c.pause();
		} else if (e.getSource() == c.updateSpeedButton) {
			c.updateSpeed();
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == c.timeSpeedSlider) {
			c.model.timeSpeed = c.timeSpeedSlider.getValue();
		} else if (e.getSource() == c.timeSlider) {
			c.model.currentTime = c.timeSlider.getValue();
			if (!c.updateTimeTimer.isRunning() && !c.model.timePaused) {
				c.updateTimeTimer.start();
				c.pauseButton.setText("Pause");
				c.model.timePaused = false;
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			c.model.moveVert(c.percent);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			c.model.moveVert(-c.percent);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			c.model.moveHoriz(-c.percent);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			c.model.moveHoriz(c.percent);
		} else if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
			c.model.zoom(c.percent);
		} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			c.model.zoom(-c.percent);
		} else if (e.getKeyCode() == KeyEvent.VK_P) {
			try {
				c.pauseButton.getActionListeners()[0].actionPerformed(
						new ActionEvent(c.pauseButton, 0, null));				
			} catch (ArrayIndexOutOfBoundsException exept) {
				System.out.println("No listeners attached to pause button");
			}
		}
		c.view.updateMap();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		c.currentX = e.getX();
		c.currentY = e.getY();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int difX = - e.getX() + c.currentX;
		int difY = e.getY() - c.currentY;
		c.currentX = e.getX();
		c.currentY = e.getY();
		c.model.moveVertPx(difY);
		c.model.moveHorizPx(difX);
		c.view.updateMap();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		c.setCurrentBus();
	}
	
	//not used
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
