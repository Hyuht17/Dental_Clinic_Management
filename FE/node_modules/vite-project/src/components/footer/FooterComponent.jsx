import { Component } from 'react';
import './footer.css';

class FooterComponent extends Component {
    render() {
        return (
            <div>
                <footer className="footer">
                    <span className="text-muted">All Rights Reserved 2021 @Dental Clinic</span>
                </footer>
            </div>
        );
    }
}

export default FooterComponent;