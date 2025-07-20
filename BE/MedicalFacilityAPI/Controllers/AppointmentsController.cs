using Microsoft.AspNetCore.Mvc;
using MedicaiFacility.Services;
using MedicaiFacility.BusinessObject;
using MedicaiFacility.Service.IService;

namespace MedicalFacilityAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AppointmentsController : ControllerBase
    {
        private readonly IAppointmentService _appointmentService;
        private readonly IMedicalExpertScheduleService _medicalExpertScheduleService;
        public AppointmentsController(IAppointmentService appointmentService, IMedicalExpertScheduleService medicalExpertScheduleService)
        {
            _appointmentService = appointmentService;
            _medicalExpertScheduleService = medicalExpertScheduleService;
        }

        // Đặt lịch hẹn mới
        [HttpPost]
        public ActionResult<string> Create([FromBody] RequestAppointment req)
        {
            var checkValidSchedule = _medicalExpertScheduleService.IsValid(req.scheduleId, req.StartDate,  req.EndDate);
            if (!checkValidSchedule.Equals("true")) {
                return checkValidSchedule;
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
            return Ok("Tạo thành công");
        }
        [HttpPut("{appointmentId:int}")]
        public ActionResult<Appointment> Update(int appointmentId, [FromBody] RequestAppointment req) {
            var existingAppointment = _appointmentService.GetById(appointmentId);

            existingAppointment.PatientId = req.PatientId;
            existingAppointment.ExpertId = req.ExpertId;
            existingAppointment.FacilityId = req.FacilityId;
            existingAppointment.Note = req.Note;
            existingAppointment.StartDate = req.StartDate;
            existingAppointment.EndDate = req.EndDate;
            existingAppointment.Status = "Pending";
            existingAppointment.UpdatedAt = DateTime.Now;
            
          var result =   _appointmentService.Update(existingAppointment);
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

        public string Status { get; set; }
    }
} 