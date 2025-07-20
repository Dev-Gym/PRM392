using MedicaiFacility.BusinessObject;
using MedicaiFacility.Service.IService;
using MedicaiFacility.Services;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;

namespace MedicalFacilityAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AppointmentsController : ControllerBase
    {
        private readonly IAppointmentService _appointmentService;
        private readonly IMedicalExpertScheduleService _medicalExpertScheduleService;
        private readonly IMedicalHistoryService _medicalHistoryService;
        public AppointmentsController(IAppointmentService appointmentService, IMedicalExpertScheduleService medicalExpertScheduleService, IMedicalHistoryService medicalHistoryService)
        {
            _appointmentService = appointmentService;
            _medicalExpertScheduleService = medicalExpertScheduleService;
            _medicalHistoryService = medicalHistoryService;
        }

        // Đặt lịch hẹn mới
        [HttpPost]
        public ActionResult<Appointment> Create([FromBody] RequestAppointment req)
        {

            req.EndDate = req.StartDate.AddMinutes(30);
            var schedule = _medicalExpertScheduleService.GetSchedulesByExpertId(req.ExpertId).FirstOrDefault(x=>x.ScheduleId==req.ScheduleId);
            if (schedule != null) {
                var date = schedule.StartDate.Date;
                req.StartDate = date+ (req.StartDate).TimeOfDay;
                req.EndDate = date+ (req.EndDate).TimeOfDay;
            }
           var checkValidSchedule = _medicalExpertScheduleService.IsValid(req.ScheduleId, req.StartDate,  req.EndDate);
            if (!checkValidSchedule.Equals("true")) {
                return null;
            }
            var newAppointment = new Appointment { 
                PatientId = req.PatientId,
                ExpertId = req.ExpertId,
                FacilityId = req.FacilityId,
                Note = req.Note,
                StartDate = req.StartDate,
                EndDate = req.EndDate,
                Status = "Pending",
                CreatedAt =DateTime.Now,
                UpdatedAt = DateTime.Now,
            };
          var result=   _appointmentService.Create(newAppointment);
            if (result == null) return BadRequest("tạo thất bại");
            return Ok(result);
        }
        [HttpPut("{appointmentId:int}")]
        public ActionResult<Appointment> Update(int appointmentId, [FromBody] RequestUpdateAppointment req) {
            var existingAppointment = _appointmentService.GetById(appointmentId);
            if (existingAppointment == null) return NotFound();
            req.EndDate = req.StartDate.AddMinutes(30);
            var schedule = _medicalExpertScheduleService.GetSchedulesByExpertId(req.ExpertId).FirstOrDefault(x => x.ScheduleId == req.ScheduleId);
            if (schedule != null)
            {
                var date = schedule.StartDate.Date;
                req.StartDate = date + (req.StartDate).TimeOfDay;
                req.EndDate = date + (req.EndDate).TimeOfDay;
            }
            var checkValidSchedule = _medicalExpertScheduleService.IsValid(req.ScheduleId, req.StartDate, req.EndDate);
            if (!checkValidSchedule.Equals("true"))
            {
                return null;
            }

            if (existingAppointment.Status == "Pending") {
                existingAppointment.StartDate = req.StartDate;
                existingAppointment.EndDate = req.EndDate;
            }

            existingAppointment.Note = req.Note;
            existingAppointment.UpdatedAt = DateTime.Now;
            var result =   _appointmentService.Update(existingAppointment);
            return Ok(result);
        }
        [HttpPut("confirm/{appointmentId:int}")]
        public ActionResult<Appointment> UpdateConfirm(int appointmentId)
        {
            var existingAppointment = _appointmentService.GetById(appointmentId);
            if(existingAppointment == null) return NotFound();
           
           
            existingAppointment.Status = "Confirmed";
            existingAppointment.UpdatedAt = DateTime.Now;

            var result = _appointmentService.Update(existingAppointment);
       
            var newHistory = new MedicalHistory
            {
                AppointmentId = appointmentId,
                Description = "đã xác nhận khám rồi",
                Status = "Pending",
                Payed = true,
                CreatedAt = DateTime.Now,
                UpdatedAt = DateTime.Now,
            };
           _medicalHistoryService.Create(newHistory);
            return Ok(result);
        }
        [HttpPut("cancelled/{appointmentId:int}")]
        public ActionResult<Appointment> UpdateCancelled(int appointmentId)
        {
            var existingAppointment = _appointmentService.GetById(appointmentId);
            var existingMedicalHistory = _medicalHistoryService.ExistingMedicalHistory(existingAppointment.AppointmentId);
            if (existingMedicalHistory != null)
            {            
                 if (existingMedicalHistory.Status == "Processing"|| existingMedicalHistory.Status == "Completed") return BadRequest("Đang diễn ra quá trình khám hoặc quá trình khám đã xong nên không thể hủy được");
                if (existingMedicalHistory.Status == "Pending") {
                    existingMedicalHistory.Status = "Cancelled";
                    _medicalHistoryService.Update(existingMedicalHistory);
                }

            }
            existingAppointment.Status = "Cancelled";
            existingAppointment.UpdatedAt = DateTime.Now;

            var result = _appointmentService.Update(existingAppointment);
            return Ok(result);
        }
        [HttpGet]
        public ActionResult<List<Appointment>> GetAppointments([FromQuery] int userId)
        {
            var appointments = _appointmentService.GetAllByUserId( userId);
            return Ok(appointments);
        }
        [HttpGet("{appointmentId:int}")]
        public ActionResult<Appointment> GetAppointmentDetail( int appointmentId)
        {
            var appointments = _appointmentService.GetById(appointmentId);
            return Ok(appointments);
        }
        [HttpPut("delete/{appointmentId:int}")]
        public ActionResult<Appointment> DeleteById(int appointmentId) {
            var existingAppointment = _appointmentService.GetById(appointmentId);
            existingAppointment.Status = "IsDelete";
          var result =   _appointmentService.Update(existingAppointment);
            return Ok(result);
        }
    }

    public class RequestAppointment {
        public int ScheduleId { get; set; }
        public int PatientId { get; set; }

        public int ExpertId { get; set; }

        public int FacilityId { get; set; }

        public string Note { get; set; }

        public DateTime StartDate { get; set; }

        public DateTime EndDate { get; set; }

    }
    public class RequestUpdateAppointment
    {

        public int ScheduleId { get; set; }
        public int PatientId { get; set; }

        public int ExpertId { get; set; }

        public int FacilityId { get; set; }

        public string Note { get; set; }

        public DateTime StartDate { get; set; }

        public DateTime EndDate { get; set; }

    }
} 