using MedicaiFacility.DataAccess.IRepostory;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MedicaiFacility.DataAccess
{
    public class HealthRecordDiseasesRepository : IHealthRecordDiseasesRepository
    {
        private readonly AppDbContext _context;
        public HealthRecordDiseasesRepository(AppDbContext context)
        {
            _context = context;
        }

        public void deleteById(int id)
        {
            var item = _context.HealthRecordDiseases.FirstOrDefault(x => x.HealthRecordDiseaseId == id);
            if (item != null) {
                item.DiseaseId = null;
                item.RecordId = null;            
                _context.HealthRecordDiseases.Remove(item);
                _context.SaveChanges();
            }
        }
    }
}
