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
        public AppointmentsController(IAppointmentService appointmentService)
        {
            _appointmentService = appointmentService;
        }

        // Đặt lịch hẹn mới
        [HttpPost]
        public IActionResult Create([FromBody] Appointment req)
        {
            _appointmentService.Create(req);
            return Ok(req);
        }

        // Lấy lịch hẹn của user
        [HttpGet("my")]
        public ActionResult<List<Appointment>> GetMyAppointments([FromQuery] int userId)
        {
            var appointments = _appointmentService.GetALlPagainationsByPatientId(pg:0, pageSize: 0, userId).list;
            return Ok(appointments);
        }
    }
} 