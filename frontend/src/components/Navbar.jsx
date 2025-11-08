import '../css/Navbar.css'

import { Link } from "react-router-dom"


function Navbar(){
    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/">Movie App</Link>
            </div>
            <div className="navbar-links">
                <Link to="/">Home</Link>
                <Link to="/Reviews">Reviews</Link>
            </div>
        </nav>
    )
}

export default Navbar