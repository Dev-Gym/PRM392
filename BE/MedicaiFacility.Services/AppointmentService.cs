using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Service.IService;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.Service
{
    public class AppointmentService : IAppointmentService
	{
		private readonly IAppointmentRepository _appointmentRepository;
		private readonly ITransactionRepository _transactionRepository;
        public AppointmentService(IAppointmentRepository appointmentRepository, ITransactionRepository transactionRepository)
        {
            _appointmentRepository = appointmentRepository;
			_transactionRepository = transactionRepository;
        }
        public Appointment Create(Appointment appointment)
		{
			var transaction = new Transaction {
				BalanceId = 1,
				UserId = appointment.PatientId,
				PaymentMethod = "MOMO",
				Amount = 150,
				TransactionStatus = "Success",
				CreatedAt = DateTime.Now,
				UpdateAt = DateTime.Now,
				TransactionType = "AppointmentTransaction"
            };
			var savingTransaction = _transactionRepository.Create(transaction);
            appointment.TransactionId = savingTransaction.TransactionId;
			appointment.Status = "Pending";
            return _appointmentRepository.Create(appointment);
		}

		public void DeleteById(int id)
		{
			_appointmentRepository.DeleteById(id);
		}

		public List<Appointment> GetAll()
		{
			return _appointmentRepository.GetAll();	
		}

        public List<Appointment> GetAllByExpertId(int expertId)
        {
			return _appointmentRepository.GetAllByExpertId(expertId);
        }

        public List<Appointment> GetAllByUserId(int userId)
        {
			return _appointmentRepository.GetALlAppointmentByUserId(userId);
        }

        public (List<Appointment> list, int totalItems) GetALlPagainations(int pg, int pageSize)
		{
			return _appointmentRepository.GetALlPagainations(pg, pageSize);
		}

        public (List<Appointment> list, int totalItems) GetALlPagainationsByExpertId(int pg, int pageSize, int expertId)
        {
			return _appointmentRepository.GetALlPagainationsByExpertId(pg, pageSize, expertId);
        }

        public (List<Appointment> list, int totalItems) GetALlPagainationsByPatientId(int pg, int pageSize, int patientId)
        {
			return _appointmentRepository.GetALlPagainationsByPatientId(pg, pageSize, patientId);
        }

        public Appointment GetById(int id)
		{
			return _appointmentRepository.GetById(id);
		}

		public Appointment Update(Appointment appointment)
        {
		
			 return _appointmentRepository.Update(appointment);	
		}
		public void CancelAllAppointmentInTime(int experId, Appointment appointment) {
			var list = _appointmentRepository.GetAll().Where(x=>x.AppointmentId!=appointment.AppointmentId&&x.StartDate == appointment.StartDate && x.EndDate == appointment.EndDate && x.ExpertId == experId&&x.Status== "Pending");
			foreach (var item in list) {
				item.Status = "Cancelled";
				item.Note = "Hủy vì đã có lịch confirmed cùng khung giờ";
				_appointmentRepository.Update(item);
			}

		}
	}
}
