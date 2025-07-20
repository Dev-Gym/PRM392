using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess;
using MedicaiFacility.DataAccess.IRepostory;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;

namespace MedicaiFacility.Repository
{
    public class MedicalExpertScheduleRepository : IMedicalExpertScheduleRepository
    {
        private readonly AppDbContext _context;

        public MedicalExpertScheduleRepository(AppDbContext context)
        {
            _context = context ?? throw new ArgumentNullException(nameof(context));
        }

        public IEnumerable<MedicalExpertSchedule> GetSchedulesByExpertId(int expertId)
        {
            var list = _context.MedicalExpertSchedules.AsQueryable();
            if (expertId > 0) {
                list = list.Where(x => x.ExpertId == expertId);
            }
            return list;
        }

        public MedicalExpertSchedule AddMedicalExpertSchedule(MedicalExpertSchedule schedule)
        {
            Console.WriteLine($"Attempting to add schedule: ExpertId={schedule.ExpertId}, Day={schedule.DayOfWeek}");
            try
            {
                _context.MedicalExpertSchedules.Add(schedule);
                _context.SaveChanges();
                Console.WriteLine($"Schedule saved successfully with ScheduleId: {schedule.ScheduleId}");
                return schedule;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error saving schedule: {ex.Message} - InnerException: {ex.InnerException?.Message}");
                throw;
            }
        }

        public void DeleteSchedulesByExpertId(int expertId)
        {
            Console.WriteLine($"Attempting to delete schedules for ExpertId: {expertId}");
            try
            {
                var schedules = _context.MedicalExpertSchedules
                    .Where(s => s.ExpertId == expertId)
                    .ToList();

                if (schedules.Any())
                {
                    _context.MedicalExpertSchedules.RemoveRange(schedules);
                    _context.SaveChanges();
                    Console.WriteLine($"Successfully deleted {schedules.Count} schedules for ExpertId: {expertId}");
                }
                else
                {
                    Console.WriteLine($"No schedules found for ExpertId: {expertId}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error deleting schedules for ExpertId {expertId}: {ex.Message} - InnerException: {ex.InnerException?.Message}");
                throw;
            }
        }

        public MedicalExpertSchedule UpdateMedicalExpertSchedule(MedicalExpertSchedule schedule)
        {
            _context.MedicalExpertSchedules.Update(schedule);
            _context.SaveChanges() ;
            return schedule;
        }

        public IEnumerable<MedicalExpertSchedule> GetAll()
        {
            return _context.MedicalExpertSchedules;
        }
    }
  
}