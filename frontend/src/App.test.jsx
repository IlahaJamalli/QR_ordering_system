import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import App from './App';

// Mock the components
vi.mock('./pages/LandingPage', () => ({
  default: () => <div data-testid="landing-page">Landing Page</div>
}));

vi.mock('./pages/TrackOrder', () => ({
  default: () => <div data-testid="track-order">Track Order</div>
}));

vi.mock('./pages/Login', () => ({
  default: () => <div data-testid="login">Login</div>
}));

vi.mock('./pages/KitchenPanel', () => ({
  default: () => <div data-testid="kitchen-panel">Kitchen Panel</div>
}));

vi.mock('./pages/WaiterPanel', () => ({
  default: () => <div data-testid="waiter-panel">Waiter Panel</div>
}));

vi.mock('./pages/KitchenMessages', () => ({
  default: () => <div data-testid="kitchen-messages">Kitchen Messages</div>
}));

vi.mock('./pages/ManagerPanel', () => ({
  default: () => <div data-testid="manager-panel">Manager Panel</div>
}));

describe('App', () => {
  const originalLocation = window.location;

  beforeEach(() => {
    delete window.location;
    window.location = { ...originalLocation, pathname: '' };
  });

  afterEach(() => {
    window.location = originalLocation;
  });

  it('renders TrackOrder component for /track path', () => {
    window.location.pathname = '/track/123';
    render(<App />);
    expect(screen.getByTestId('track-order')).toBeInTheDocument();
  });

  it('renders LandingPage component for /menu path', () => {
    window.location.pathname = '/menu';
    render(<App />);
    expect(screen.getByTestId('landing-page')).toBeInTheDocument();
  });

  it('renders Login component for /login path', () => {
    window.location.pathname = '/login';
    render(<App />);
    expect(screen.getByTestId('login')).toBeInTheDocument();
  });

  it('renders KitchenPanel component for /kitchen-panel path', () => {
    window.location.pathname = '/kitchen-panel';
    render(<App />);
    expect(screen.getByTestId('kitchen-panel')).toBeInTheDocument();
  });

  it('renders WaiterPanel component for /waiter-panel path', () => {
    window.location.pathname = '/waiter-panel';
    render(<App />);
    expect(screen.getByTestId('waiter-panel')).toBeInTheDocument();
  });

  it('renders KitchenMessages component for /kitchen-messages path', () => {
    window.location.pathname = '/kitchen-messages';
    render(<App />);
    expect(screen.getByTestId('kitchen-messages')).toBeInTheDocument();
  });

  it('renders ManagerPanel component for /manager-panel path', () => {
    window.location.pathname = '/manager-panel';
    render(<App />);
    expect(screen.getByTestId('manager-panel')).toBeInTheDocument();
  });

  it('renders unknown path message for invalid paths', () => {
    window.location.pathname = '/invalid-path';
    render(<App />);
    expect(screen.getByText('Unknown path')).toBeInTheDocument();
  });
}); 