using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.BusinessObject.Pagination;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess
{
    public class HealthRecordRepository : IHealthRecordRepository
    {
        private readonly AppDbContext _Context;
        public HealthRecordRepository(AppDbContext context)
        {
            _Context = context;
        }

        public void deleteById(int id)
        {
            var item = _Context.HealthRecords.FirstOrDefault(x => x.RecordId == id);
            if (item != null) { 
                _Context.HealthRecords.Remove(item);
                _Context.SaveChanges();
            }
        }

        public HealthRecord FindById(int id)
        {
            return _Context.HealthRecords.Include(x=>x.HealthRecordDiseases).ThenInclude(x=>x.Disease).ThenInclude(x=>x.Department)
           .FirstOrDefault(x=>x.RecordId== id);
        }

        public List<HealthRecord> GetAll()
        {
            var healthRecords = _Context.HealthRecords
       
            .ToList();
            return healthRecords;
        }
        public (List<HealthRecord>, int totalItem) findAllWithPagination(int pg, int pageSize) {
            var healthRecords = _Context.HealthRecords
             
             .ToList();
            int totalItem = healthRecords.Count();
            var pager = new Pager(totalItem, pg, pageSize);
            int skipItem = (pg-1)*pageSize;
            var data = healthRecords.Skip(skipItem).Take(pager.Pagesize).ToList();
            return (data, totalItem);
        }

        public void Save(HealthRecord healthRecord)
        {
            _Context.HealthRecords.Add(healthRecord);
            _Context.SaveChanges();
        }

        public void Udpate(HealthRecord healthRecord)
        {
            _Context.HealthRecords.Update(healthRecord);
            _Context.SaveChanges();
        }


        public HealthRecord findByMedicalHistoryId(int hisId) => _Context.HealthRecords.Include(x => x.HealthRecordDiseases).ThenInclude(x => x.Disease).ThenInclude(x => x.Department).FirstOrDefault(x => x.MedicalHistoryId == hisId);


    }
}
