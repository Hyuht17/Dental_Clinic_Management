import './NavbarComponent.css'; // Thêm file css
import logo from '../../assets/first-aid-kit.png'; // Thêm đường dẫn đến ảnh logo
import homelogo from '../../assets/home.png'; // Thêm đường dẫn đến ảnh home
import aboutlogo from '../../assets/about.png'; // Thêm đường dẫn đến ảnh about
import contactlogo from '../../assets/contacts.png'
function Navbar() {
        return(
            <header className='header'>
                <a href="/" className="logo">
                <img src={logo} alt="Dental Clinic Logo" className="logo-image" /> {/* Thêm ảnh logo */}
                Dental Clinic
            </a>
                <nav className='navbar'>
                    <a href='/'><img src={homelogo} className='logo-image'/>Home</a>
                    <a href='/'><img src={aboutlogo} className='logo-image'/>About Us</a>
                    <a href='/'><img src={contactlogo} className='logo-image'/>Contact</a>
                    <a href='/'>Home</a>
                    <a href='/'>Home</a>
                </nav>
            </header>
        );
};
export default Navbar
