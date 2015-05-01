package ru.ioffe.school.buses.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.ioffe.school.buses.data.Bus;
import ru.ioffe.school.buses.data.Point;

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
		} else if (e.getSource() == c.showPerson) {
			c.view.setShowPerson(!c.view.isShowPerson());
			c.showPerson.setText((c.view.isShowPerson()? "Hide" : "Show") +  " persons");
		} else if (e.getSource() == c.showBus) {
			c.view.setShowBus(!c.view.isShowBus());
			c.showBus.setText((c.view.isShowBus()? "Hide" : "Show") +  " buses");
		} else if (e.getSource() == c.showCrossroads) {
			c.view.setShowCrossroads(!c.view.isShowCrossroads());
			c.showCrossroads.setText((c.view.isShowCrossroads()? "Hide" : "Show") +  " cross-roads");
			c.view.updateMap();
		} else if (e.getSource() == c.showWay) {
			c.view.setShowWay(!c.view.isShowWay());
			c.showWay.setText((c.view.isShowWay()? "Hide" : "Show") +  " route");
		} else if (e.getSource() == c.busVeryBig) {
			c.view.setBusSize(GUIView.VERY_BIG_SIZE);
		} else if (e.getSource() == c.busBig) {
			c.view.setBusSize(GUIView.BIG_SIZE);
		} else if (e.getSource() == c.busMedium) {
			c.view.setBusSize(GUIView.MEDIUM_SIZE);
		} else if (e.getSource() == c.busSmall) {
			c.view.setBusSize(GUIView.SMALL_SIZE);
		} else if (e.getSource() == c.busTiny) {
			c.view.setBusSize(GUIView.TINY_SIZE);
		} else if (e.getSource() == c.personVeryBig) {
			c.view.setPersonSize(GUIView.VERY_BIG_SIZE);
		} else if (e.getSource() == c.personBig) {
			c.view.setPersonSize(GUIView.BIG_SIZE);
		} else if (e.getSource() == c.personMedium) {
			c.view.setPersonSize(GUIView.MEDIUM_SIZE);
		} else if (e.getSource() == c.personSmall) {
			c.view.setPersonSize(GUIView.SMALL_SIZE);
		} else if (e.getSource() == c.personTiny) {
			c.view.setPersonSize(GUIView.TINY_SIZE);
		} else if (e.getSource() == c.crossroadVeryBig) {
			c.view.setCrossroadSize(GUIView.VERY_BIG_SIZE);
			c.view.updateMap();
		} else if (e.getSource() == c.crossroadBig) {
			c.view.setCrossroadSize(GUIView.BIG_SIZE);
			c.view.updateMap();
		} else if (e.getSource() == c.crossroadMedium) {
			c.view.setCrossroadSize(GUIView.MEDIUM_SIZE);
			c.view.updateMap();
		} else if (e.getSource() == c.crossroadSmall) {
			c.view.setCrossroadSize(GUIView.SMALL_SIZE);
			c.view.updateMap();
		} else if (e.getSource() == c.crossroadTiny) {
			c.view.setCrossroadSize(GUIView.TINY_SIZE);
			c.view.updateMap();
		} else if (e.getSource() == c.exitItem) {
			System.exit(0);
		} else if (e.getSource() == c.openReport) {
			c.selectingRep = true;
			c.chooser.showOpenDialog(c);
		} else if (e.getSource() == c.openShortReport) {
			c.selectingRep = false;
			c.chooser.showOpenDialog(c);
		} else if (e.getSource() == c.emulateItem) {
			c.showParamPane();
			if (c.haveToEmulate) {
				c.model.emulate(null);
				c.initBusPanel();
				c.busesInited = true;				
			}
			c.haveToEmulate = true;
		} else if (e.getSource() == c.chooser) {
			if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION))
				return;
			File file = c.chooser.getSelectedFile();
			c.chooseFile(file);
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

	@Override
	public void mouseClicked(MouseEvent e) {
		int delta = c.view.busSize + 5;
		double pxSize = c.model.totalGUIWidth /
				(c.model.right - c.model.left);
		int clx = e.getX();
		int cly = e.getY();
		for (int i = 0; i < c.model.buses.size(); i++) {
			Bus bus = c.model.buses.get(i);
			for (Point p : bus.getPosition(c.model.currentTime)) {
				if (p == null)
					continue;
				double difX = p.getX() - c.model.left;
				double difY = - p.getY() + c.model.up;
				int x = (int) (difX * pxSize);
				int y = (int) (difY * pxSize);
				if (clx > x - delta && clx < x + delta && cly > y - delta && cly < y + delta) {
					c.busList.setSelectedIndex(i + 1);
					c.model.currentBus = bus;
					c.updateBusInfo();
					break;
				}
			}
		}
	}
	
	//not used
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {		
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
