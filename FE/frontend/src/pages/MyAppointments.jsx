import React, { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../context/AppContext';
import axios from 'axios';
import { toast } from 'react-toastify';
import Modal from 'react-modal'; // Thêm thư viện react-modal

const MyAppointments = () => {
  const { backendUrl, token, userId, userData, loadUserProfileData } = useContext(AppContext);
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [payment, setPayment] = useState(0);
  const [selectedAppointment, setSelectedAppointment] = useState(null); // Appointment đang được chọn để hiện hóa đơn
  const [isBillModalOpen, setIsBillModalOpen] = useState(false); // Trạng thái mở/đóng popup



  // Hàm định dạng ngày
  const slotDateFormat = (slotDate) => {
      const months = ["", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
      const date = new Date(slotDate);
      const day = date.getDate();
      const month = months[date.getUTCMonth() + 1];
      const year = date.getUTCFullYear();
      return `${day} ${month} ${year}`;
  };

  // Hàm định dạng giờ
  const formatTime = (time) => {
      const [hour, minute] = time.split(':');
      const intHour = parseInt(hour, 10);
      const period = intHour >= 12 ? 'PM' : 'AM';
      const formattedHour = intHour % 12 || 12;
      return `${formattedHour}:${minute} ${period}`;
  };

  // Lấy danh sách lịch hẹn
  const getUserAppointments = async () => {
      try {
          const { data } = await axios.get(`${backendUrl}/api/user/appointments?userId=${userId}`, { headers: { token } });
          setAppointments(data.appointments.reverse());
      } catch (error) {
          console.log(error);
          toast.error(error.message);
      }
  };

  // Hủy lịch hẹn
  const cancelAppointment = async (appointmentId) => {
      try {
          const { data } = await axios.post(backendUrl + '/api/user/cancel-appointment', { appointmentId }, { headers: { token } });
          if (data.success) {
              toast.success(data.message);
              getUserAppointments();
          } else {
              toast.error(data.message);
          }
      } catch (error) {
          console.log(error);
          toast.error(error.message);
      }
  };

  // Hàm mở modal bill
  const openBillModal = (appointment) => {
      loadUserProfileData(); // Load thông tin user
      setSelectedAppointment(appointment); // Lưu lịch hẹn đã chọn
      setIsBillModalOpen(true); // Mở modal
  };

  // Hàm đóng modal bill
  const closeBillModal = () => {
      setIsBillModalOpen(false);
      setSelectedAppointment(null);
  };

  // Hàm thanh toán và cập nhật trạng thái is_paid
  const payAppointment = async () => {
      if (selectedAppointment) {
          try {
              const { data } = await axios.post(`${backendUrl}/api/user/pay-appointment`, {
                  appointmentId: selectedAppointment.appointmentId
              }, { headers: { token } });
              
              if (data.success) {
                  toast.success('Payment successful');
                  setSelectedAppointment(prev => ({ ...prev, isPaid: true })); // Cập nhật trạng thái isPaid
                  getUserAppointments(); // Cập nhật lại danh sách lịch hẹn
              } else {
                  toast.error(data.message);
              }
          } catch (error) {
              console.log(error);
              toast.error(error.message);
          }
      }
  };

  useEffect(() => {
      if (token) {
          getUserAppointments();
      }
  }, [token]);

  return (
      <div>
          <p className="pb-3 mt-12 text-lg font-medium text-gray-600 border-b">My appointments</p>
          <div className="grid gap-4">
              {appointments.map((item, index) => (
                  <div key={index} className="grid grid-cols-[1fr_2fr] gap-4 sm:flex sm:gap-6 py-4 border-b">
                      <div>
                          <img className="w-36 bg-[#EAEFFF]" src={item.dentist.imgUrl} alt="" />
                      </div>
                      <div className="flex-1 text-sm text-[#5E5E5E]">
                          <p className="text-[#262626] text-base font-semibold">{item.dentist.name}</p>
                          <p>{item.dentist.speciality}</p>
                          <p className=" mt-1">
                              <span className="text-sm text-[#3C3C3C] font-medium">Date & Time:</span>{' '}
                              {slotDateFormat(item.appointmentDate)} | {formatTime(item.appointmentTime)}
                          </p>
                      </div>
                      <div className="flex flex-col gap-2 justify-end text-sm text-center">
                          <button
                              onClick={() => openBillModal(item)} // Gắn sự kiện mở popup
                              className="text-[#696969] sm:min-w-48 py-2 border rounded hover:bg-blue-600 hover:text-white transition-all duration-300"
                          >
                              View Bill
                          </button>
                          {!item.cancelled && !item.isCompleted && (
                              <button
                                  onClick={() => cancelAppointment(item.appointmentId)}
                                  className="text-[#696969] sm:min-w-48 py-2 border rounded hover:bg-red-600 hover:text-white transition-all duration-300"
                              >
                                  Cancel appointment
                              </button>
                          )}
                          {item.cancelled && <button className="sm:min-w-48 py-2 border border-red-500 rounded text-red-500">Appointment cancelled</button>}
                      </div>
                  </div>
              ))}
          </div>

          {/* Modal hiển thị bill */}
          <Modal
isOpen={isBillModalOpen}
onRequestClose={closeBillModal}
className="modal"
overlayClassName="overlay"
>
<div className="p-4">
  <h2 className="text-lg font-bold mb-4">Bill Details</h2>
  {selectedAppointment && (
    <div className="text-sm">
      <p>
        <span className="font-medium">Patient Name: </span> {userData.fullName}
      </p>
      <p>
        <span className="font-medium">Email:</span> {userData.email}
      </p>
      <p>
        <span className="font-medium">Phone:</span> {userData.phone}
      </p>
      <p>
        <span className="font-medium">Address:</span> {userData.address}
      </p> 
      <br />
      <p>
        <span className="font-medium">Dentist:</span> {selectedAppointment.dentist.name}
      </p>
      <p>
        <span className="font-medium">Speciality:</span> {selectedAppointment.dentist.speciality}
      </p>
      <p>
        <span className="font-medium">Date:</span> {slotDateFormat(selectedAppointment.appointmentDate)}
      </p>
      <p>
        <span className="font-medium">Time:</span> {formatTime(selectedAppointment.appointmentTime)}
      </p>
      <p>
        <span className="font-medium">Price:</span> ${100}
      </p>
    </div>
  )}
  <div className="flex space-x-4 mt-4"> {/* Cách các nút ra bằng space-x-4 */}
    <button
      onClick={() => {
        // Update trạng thái thanh toán
        axios.post(`${backendUrl}/api/user/update-payment`, { appointmentId: selectedAppointment.appointmentId }, { headers: { token } })
          .then(response => {
            if (response.data.success) {
              toast.success("Payment successful");
            } else {
              toast.error("Payment failed");
            }
          });
      }}
      className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
    >
      Pay
    </button>
    <button
      onClick={closeBillModal}
      className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
    >
      Close
    </button>
  </div>
</div>
</Modal>


      </div>
  );
};

export default MyAppointments;
