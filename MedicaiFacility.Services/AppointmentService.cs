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
        public AppointmentService(IAppointmentRepository appointmentRepository)
        {
            _appointmentRepository = appointmentRepository;
        }
        public void Create(Appointment appointment)
		{
			_appointmentRepository.Create(appointment);
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

		public void Update(Appointment appointment)
		{
			_appointmentRepository.Update(appointment);	
		}
	}
}
