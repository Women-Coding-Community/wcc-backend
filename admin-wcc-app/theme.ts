import {createTheme} from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#226488',
      dark: '#1b1919',
      light: '#F6FAFE',
      contrastText: '#ffffff'
    },
    secondary: {
      main: '#84B1D0',
      light: '#FFDBD0',
      contrastText: '#ffffff'
    },
    background: {
      default: '#F6FAFE',
      paper: '#FFFFFF'
    }
  },
  shape: {borderRadius: 10},
  typography: {
    fontFamily: ['Roboto, "Helvetica Neue", Arial, sans-serif'].join(',')
  }
});

export default theme;
