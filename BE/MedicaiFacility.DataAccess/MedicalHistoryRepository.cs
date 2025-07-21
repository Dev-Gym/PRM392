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
    public class MedicalHistoryRepository : IMedicalHistoryRepository
    {
        private readonly AppDbContext _Context;
        public MedicalHistoryRepository(AppDbContext Context)
        {
            _Context = Context;
        }
        public MedicalHistory Create(MedicalHistory medicalHistory)
        {
           _Context.MedicalHistories.Add(medicalHistory);
            _Context.SaveChanges();
            return medicalHistory;
        }

        public void DeleteById(int id)
        {
            var item = _Context.MedicalHistories.FirstOrDefault(x=>x.HistoryId == id);
            if (item != null) { 
                _Context.MedicalHistories.Remove(item);
                _Context.SaveChanges(); 
            }
        }

        public List<MedicalHistory> GetAll()
        {
            return _Context.MedicalHistories.Include(x=>x.Appointment).ToList();  
        }

        public (List<MedicalHistory> list, int totalItems) GetALlPagainations(int pg, int pageSize)
        {
            var list = _Context.MedicalHistories.OrderByDescending(x=>x.HistoryId).Include(x => x.Appointment).ThenInclude(x=>x.Expert).ThenInclude(x=>x.Expert)
              .Include(x => x.Appointment).ThenInclude(x => x.Patient).ThenInclude(x => x.PatientNavigation)
              .Include(x=>x.Appointment).ThenInclude(x=>x.Transaction)
                .ToList();
            int total = list.Count();  
           Pager pager = new Pager(total,pg,pageSize);
            int skipItem = (pg - 1) * pageSize;
            var data = list.Skip(skipItem).Take(pager.Pagesize).ToList();
            return (data, total);
        }

        public MedicalHistory GetById(int id)
        {
            return _Context.MedicalHistories.Include(x => x.Appointment).ThenInclude(x => x.Expert).ThenInclude(x => x.Expert)
              .Include(x => x.Appointment).ThenInclude(x => x.Patient).ThenInclude(x => x.PatientNavigation).FirstOrDefault(x => x.HistoryId==id);
        }

        public MedicalHistory Update(MedicalHistory medicalHistory)
        {
           _Context.MedicalHistories.Update(medicalHistory);
            _Context.SaveChanges(); 
            return medicalHistory;
        }

        public (List<MedicalHistory> list, int totalItems) GetALlPagainationsByPatientId(int pg, int pageSize,int patientId)
        {
            var list = _Context.MedicalHistories.Where(x=>x.Appointment.PatientId==patientId).OrderByDescending(x => x.HistoryId).Include(x => x.Appointment).ThenInclude(x => x.Expert).ThenInclude(x => x.Expert)
              .Include(x => x.Appointment).ThenInclude(x => x.Patient).ThenInclude(x => x.PatientNavigation)
                .ToList();
            int total = list.Count();
            Pager pager = new Pager(total, pg, pageSize);
            int skipItem = (pg - 1) * pageSize;
            //var data = list.Skip(skipItem).Take(pager.Pagesize).ToList();
            return (list, total);
        }

        public (List<MedicalHistory> list, int totalItems) GetALlPagainationsByExpertId(int pg, int pageSize, int expertId)
        {
            var list = _Context.MedicalHistories.Where(x => x.Appointment.ExpertId == expertId).OrderByDescending(x => x.HistoryId).Include(x => x.Appointment).ThenInclude(x => x.Expert).ThenInclude(x => x.Expert)
              .Include(x => x.Appointment).ThenInclude(x => x.Patient).ThenInclude(x => x.PatientNavigation)
                .ToList();
            int total = list.Count();
            Pager pager = new Pager(total, pg, pageSize);
            int skipItem = (pg - 1) * pageSize;
            //var data = list.Skip(skipItem).Take(pager.Pagesize).ToList();
            return (list, total);
        }

        public List<MedicalHistory> GetAllByUserId(int userId)
        {
            var medicalHistories = _Context.MedicalHistories
          .Where(m => _Context.Appointments
              .Where(a => userId <= 0 || a.PatientId == userId || a.ExpertId == userId)
              .Select(a => a.AppointmentId).ToList()
              .Contains((int)m.AppointmentId))
          .ToList();

            return medicalHistories;
        }

        public MedicalHistory ExistingMedicalHistory(int appointmentId)
        {
            return _Context.MedicalHistories.FirstOrDefault(x => x.AppointmentId == appointmentId);
        }
    }
}
