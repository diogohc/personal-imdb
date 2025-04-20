import './css/App.css'
import Home from './pages/Home'
import Reviews from './pages/Reviews'
import {Routes, Route} from 'react-router-dom'
import Navbar from './components/Navbar'
import RegistrationSuccessful from './components/authentication/RegistrationSuccessful'
import Registration from './components/authentication/Registration'
import Login from './components/authentication/Login'

function App() {
  return (
    <div>
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path='/' element={<Registration />} />
          <Route path='/home' element={<Home />} />
          <Route path='/reviews' element={<Reviews />} />
          <Route path='/registration' element={<Registration />} />
          <Route path='/registrationSuccessful' element={<RegistrationSuccessful />} />
          <Route path='/login' element={<Login />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
