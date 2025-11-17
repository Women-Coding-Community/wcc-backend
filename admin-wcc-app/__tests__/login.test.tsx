import {render, screen} from '@testing-library/react';
import LoginPage from '@/pages/login';
import {AuthProvider} from '@/components/AuthProvider';

jest.mock('next/router', () => require('next-router-mock'));

describe('LoginPage', () => {
  it('renders title and form', () => {
    render(
        <AuthProvider>
          <LoginPage/>
        </AuthProvider>
    );
    expect(screen.getByText(/Women Coding Community/i)).toBeInTheDocument();
    expect(screen.getByRole('button', {name: /sign in/i})).toBeInTheDocument();
  });
});
