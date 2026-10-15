import { render, screen } from '@testing-library/react';
import App from './App';

test('oturum yokken giriş ekranını gösterir', () => {
  localStorage.clear();
  render(<App />);
  expect(screen.getByText(/FilmBox/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText('Email')).toBeInTheDocument();
  expect(screen.getByPlaceholderText('Şifre')).toBeInTheDocument();
});
