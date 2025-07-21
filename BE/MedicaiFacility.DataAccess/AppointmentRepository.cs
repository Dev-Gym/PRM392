using MedicaiFacility.BusinessObject;
using MedicaiFacility.BusinessObject.Pagination;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess
{
    public class AppointmentRepository : IAppointmentRepository
    {
        private readonly AppDbContext _context;
        public AppointmentRepository(AppDbContext context)
        {
            _context = context;
        }
        public Appointment Create(Appointment appointment)
        {
            try
            {

                var conflict = _context.Appointments.FirstOrDefault(x =>
             x.Status == "Confirmed" &&
             (
                 (appointment.StartDate < x.EndDate) &&
                 (appointment.EndDate > x.StartDate)
             )
            );

                if (conflict!=null)
                {
                    return null;
                }

          

                _context.ChangeTracker.Clear();
                _context.Appointments.Add(appointment);
                _context.SaveChanges();
                return appointment;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return null;
            }
        }

        public void DeleteById(int id)
        {
            var item = _context.Appointments.FirstOrDefault(x => x.AppointmentId == id);
            if (item != null)
            {
                _context.Appointments.Remove(item);
                _context.SaveChanges();
            }
        }

        public List<Appointment> GetAll()
        {
            return _context.Appointments.OrderByDescending(x => x.AppointmentId)
                .Include(x => x.Expert).ThenInclude(x => x.Expert).Include(x => x.Patient).ThenInclude(x => x.PatientNavigation).Include(x => x.Facility)
                .ToList();
        }

        public (List<Appointment> list, int totalItems) GetALlPagainations(int pg, int pageSize)
        {
            var list = _context.Appointments.OrderByDescending(x => x.AppointmentId).Include(x => x.Transaction)
                .Include(x => x.Expert).ThenInclude(x => x.Expert).Include(x => x.Patient).ThenInclude(x => x.PatientNavigation).Include(x => x.Facility)
                .ToList();
            var total = list.Count();
            Pager pager = new Pager(total, pg, pageSize);
            int skip = (pg - 1) * pageSize;
            //var data = list.Skip(skip).Take(pager.Pagesize).ToList();
            return (list, total);
        }

        public Appointment GetById(int id)
        {
            return _context.Appointments.OrderByDescending(x => x.AppointmentId)
                .Include(x => x.Expert).ThenInclude(x => x.Expert).Include(x => x.Patient).ThenInclude(x => x.PatientNavigation)
                .Include(x => x.Transaction).Include(x => x.Facility)
                .FirstOrDefault(x => x.AppointmentId == id);
        }

        public List<Appointment> GetAllByExpertId(int expertId) => _context.Appointments.Where(x => x.ExpertId == expertId).ToList();

        public Appointment Update(Appointment appointment)
        {

            _context.ChangeTracker.Clear();

            _context.Appointments.Update(appointment);
            _context.SaveChanges();
            return appointment;
        }


        public (List<Appointment> list, int totalItems) GetALlPagainationsByPatientId(int pg, int pageSize, int patientId)
        {
            var list = _context.Appointments.Where(x => x.PatientId == patientId).OrderByDescending(x => x.AppointmentId).Include(x => x.Transaction)
                .Include(x => x.Expert).ThenInclude(x => x.Expert).Include(x => x.Patient).ThenInclude(x => x.PatientNavigation).Include(x => x.Facility)
                .ToList();
            //var list = _context.Appointments.ToList();
            var total = list.Count();

            int skip = (pg - 1) * pageSize;
            //   var data = list.Skip(skip).Take(pageSize).ToList();
            return (list, total);
        }

        public (List<Appointment> list, int totalItems) GetALlPagainationsByExpertId(int pg, int pageSize, int expertId)
        {
            var list = _context.Appointments.Where(x => x.ExpertId == expertId).OrderByDescending(x => x.AppointmentId).Include(x => x.Transaction)
                .Include(x => x.Expert).ThenInclude(x => x.Expert).Include(x => x.Patient).ThenInclude(x => x.PatientNavigation).Include(x => x.Facility)
                .ToList();
            var total = list.Count();

            int skip = (pg - 1) * pageSize;
            var data = list.Skip(skip).Take(pageSize).ToList();
            return (data, total);
        }
        public List<Appointment> GetALlAppointmentByUserId(int userID)
        {
            var list = _context.Appointments.AsQueryable();
            if (userID > 0)
            {
                list = list.Where(x => x.ExpertId == userID || x.PatientId == userID);

            }
            list = list.OrderByDescending(x => x.AppointmentId);

            return list.ToList();
        }
    }
}
